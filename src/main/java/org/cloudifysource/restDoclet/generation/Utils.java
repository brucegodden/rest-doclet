/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.cloudifysource.restDoclet.generation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocAnnotation;
import org.cloudifysource.restDoclet.docElements.DocController;
import org.cloudifysource.restDoclet.docElements.DocHttpMethod;
import org.cloudifysource.restDoclet.docElements.DocMethod;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;

/**
 *
 * @author yael
 *
 */
public final class Utils {
	/**
	 *
	 * @param annotations .
	 * @param annotationName .
	 * @return The annotation.
	 */
	protected static DocAnnotation getAnnotation(final Iterable<DocAnnotation> annotations, final String annotationName) {
		DocAnnotation requestedAnnotation = null;
		if (annotations != null) {
			for (DocAnnotation annotation : annotations) {
				if (annotation.getName().equals(annotationName)) {
					requestedAnnotation = annotation;
					break;
				}
			}
		}
		return requestedAnnotation;
	}

	/**
	 *
	 * @param methodDoc .
	 * @return The parameters' comments.
	 */
	protected static Map<String, String> getParamTagsComments(
			final MethodDoc methodDoc) {
		Map<String, String> paramComments = new HashMap<String, String>();
		for (ParamTag paramTag : methodDoc.paramTags()) {
			paramComments.put(paramTag.parameterName(),
					paramTag.parameterComment());
		}
		return paramComments;
	}

	/**
	 *
	 * @param classDoc .
	 * @param annotations .
	 * @return true if the class should be filtered out, false otherwise.
	 */
	protected static boolean filterOutControllerClass(final ClassDoc classDoc, final RestAnnotations annotations) {
    String name = classDoc.qualifiedTypeName();
		return (annotations.getAnnotation(RestDocConstants.DocAnnotationTypes.CONTROLLER) == null
				|| RestDocConstants.ADMIN_API_CONTROLLER_CLASS_NAME.equals(name));
		// return
		// !(classDoc.qualifiedTypeName().equals(RestDocConstants.SERVICE_CONTROLLER_CLASS_NAME));
	}


	/**
	 *
	 * @param annotations .
	 * @return true if the method should be filtered out, false otherwise.
	 */
	protected static boolean filterOutMethod(final RestAnnotations annotations) {
		return annotations.getAnnotation(RestDocConstants.DocAnnotationTypes.INTERNAL_METHOD) != null
				|| annotations.requestMappingAnnotation() == null;
	}

	@SuppressWarnings("unused")
	private static void printMethodsToFile(final List<DocController> controllers,
			final String fileName) throws IOException {
		PrintStream print = null;
		try {
			print = new PrintStream(new File(fileName));
			for (DocController docController : controllers) {
				Collection<DocMethod> methods = docController.getMethods()
						.values();
				print.println("*****************************************");
				print.println("Controller " + docController.getName());
				print.println("*****************************************");
				for (DocMethod docMethod : methods) {
					List<DocHttpMethod> httpMethods = docMethod
							.getHttpMethods();
					for (DocHttpMethod docHttpMethod : httpMethods) {
						print.println("method "
								+ docHttpMethod.getMethodSignatureName());
						// print.println("				uri " + docMethod.getUri());
						// print.println("				request method " +
						// docHttpMethod.getHttpMethodName());
					}
				}
			}
		} finally {
			if (print != null) {
				print.flush();
				print.close();
			}
		}
	}

	/**
	 *
	 * @param body .
	 * @return The body in Json format.
	 * @throws IOException .
	 */
	public static String getIndentJson(final String body) throws IOException {
		if (StringUtils.isBlank(body)) {
			return null;
		}

		StringWriter out = new StringWriter();
		JsonParser parser = null;
		JsonGenerator gen = null;
		try {
			JsonFactory fac = new JsonFactory();

			parser = fac.createJsonParser(new StringReader(body));
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(parser);
			// Create pretty printer:
			gen = fac.createJsonGenerator(out);
			gen.useDefaultPrettyPrinter();
			// Write:
			mapper.writeTree(gen, node);

			gen.close();
			parser.close();

			return out.toString();

		} finally {
			out.close();
			if (gen != null) {
				gen.close();
			}
			if (parser != null) {
				parser.close();
			}
		}

	}

	private Utils() {

	}
}

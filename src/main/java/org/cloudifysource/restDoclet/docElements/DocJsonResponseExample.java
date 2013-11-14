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
package org.cloudifysource.restDoclet.docElements;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.generation.Utils;

import com.google.common.base.Optional;
import com.sun.javadoc.AnnotationDesc;

/**
 *
 * @author yael
 *
 */
public class DocJsonResponseExample extends DocAnnotation {
	public DocJsonResponseExample(final AnnotationDesc annotationDesc) {
		super(annotationDesc);
	}

	public String getComments() {
		return getValue(RestDocConstants.JSON_RESPONSE_EXAMPLE_COMMENTS).or("").toString();
	}

	/**
	 *
	 * @return The response body in Json format.
	 * @throws IOException .
	 */
	public String generateJsonResponseBody() throws IOException {
		String jsonResponseBody = "{\"status\": \"" + getValue(RestDocConstants.JSON_RESPONSE_EXAMPLE_STATUS) + "\"";

    Optional value = getValue(RestDocConstants.JSON_RESPONSE_EXAMPLE_RESPONSE);
		if (value.isPresent()) {
      jsonResponseBody += ",\"response\": " + value.get() + "}";
      jsonResponseBody = Utils.getIndentJson(jsonResponseBody);
		} else {
      jsonResponseBody += "}";
		}

		return jsonResponseBody;
	}

//		String value = attrValue.toString().replace("\\\"", "\"").trim();

}

package org.cloudifysource.restDoclet.exampleGenerators;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import org.apache.commons.lang.ClassUtils;
import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.cloudifysource.restDoclet.generation.AnnotationReader;
import org.cloudifysource.restDoclet.generation.QueryParamGenerator;
import org.cloudifysource.restDoclet.generation.Utils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import com.google.common.base.Joiner;
import com.sun.javadoc.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author edward
 */
public class ExampleGenerator {

  private final ObjectCreator objectCreator_;
  private final AnnotationReader annotationReader = new AnnotationReader();

  public ExampleGenerator(ObjectCreator objectCreator) {
    objectCreator_ = objectCreator;
  }

  public DocJsonResponseExample exampleResponse(final MethodDoc methodDoc) throws ClassNotFoundException, IllegalAccessException, IOException {
    Object newInstance = createObjectFromType(methodDoc.returnType());

    try {
      String generateExample = new ObjectMapper()
              .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
              .writeValueAsString(newInstance);

      return new DocJsonResponseExample(Utils.getIndentJson(generateExample), "");
    } catch (Exception e) {
      return new DocJsonResponseExample(RestDocConstants.FAILED_TO_CREATE_REQUEST_EXAMPLE + "."
                        + "\n"
                        + "Parameter type: " + methodDoc.returnType() + "."
                        + "\n"
                        + "The exception caught was " + e, "");
    }
  }

  public DocJsonRequestExample exampleRequest(final MethodDoc methodDoc) throws IllegalAccessException, ClassNotFoundException, IntrospectionException {
    List<DocParameter> params = generateParameters(methodDoc);
    Class clazz = null;
    for (DocParameter docParameter : params) {
      if (docParameter.getLocation().contains("RequestBody")) {
        clazz = docParameter.getParamClass();
        break;
      }
    }
		if (clazz == null) {
			return DocJsonRequestExample.EMPTY;
		}
    Object newInstance = createObjectFromType(methodDoc.returnType());
    try {
      String generateExample = new ObjectMapper()
              .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
              .writeValueAsString(newInstance);

      return new DocJsonRequestExample(Utils.getIndentJson(generateExample), "");
    } catch (Exception e) {
      return new DocJsonRequestExample(RestDocConstants.FAILED_TO_CREATE_REQUEST_EXAMPLE + "."
              + "\n"
              + "Parameter type: " + methodDoc.returnType() + "."
              + "\n"
              + "The exception caught was " + e, "");
    }
  }

  public Object createObjectFromType(com.sun.javadoc.Type type) throws ClassNotFoundException, IllegalAccessException {
    return isParameterized(type)
            ? objectCreator_.createParameterizedObject(
                      Class.forName(classDescriptorFromType(type)),
                      javadocTypesToClasses(type.asParameterizedType().typeArguments()))
            : objectCreator_.createObject(Class.forName(classDescriptorFromType(type)));
  }

  private boolean isParameterized(Type type) {
    return type.asParameterizedType() != null;
  }

  Class[] javadocTypesToClasses(Type[] types) throws ClassNotFoundException {
    Class[] classes = new Class[types.length];
    for (int i = 0; i < types.length; i++) {
      classes[i] = Class.forName(types[i].qualifiedTypeName());
    }
    return classes;
  }

  private String classDescriptorFromType(Type type) {
    if (type.asClassDoc() != null) {
      ClassDoc cd = type.asClassDoc();
      ClassDoc outer = cd.containingClass();
      String qualifiedName;
      if (outer == null) {
        qualifiedName = cd.qualifiedName();
      }
      else {
        String simpleName = cd.name();
        simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);
        qualifiedName = outer.qualifiedName() + '$' + simpleName;
      }
      return qualifiedName;
    }
    else {
      return type.qualifiedTypeName();
    }
  }

  private List<DocParameter> generateParameters(final MethodDoc methodDoc) throws ClassNotFoundException,
          IntrospectionException {
    List<DocParameter> paramsList = new LinkedList<DocParameter>();

    for (Parameter parameter : methodDoc.parameters()) {
      String name = parameter.name();
      List<AnnotationDesc> annotations = Arrays.asList(parameter.annotations());
      if (annotations.isEmpty()) {
        continue;
      }
      Class<?> clazz = ClassUtils.getClass(parameter.type().qualifiedTypeName());
      String location = paramAnnotationTypeString(annotations);
      DocParameter docParameter = new DocParameter(name, clazz, location, annotationReader.read(annotations, null).requestParamAnnotation().orNull());
      if (location == null || location.isEmpty()) {
        paramsList.addAll(generateQueryParameters(parameter));
      }
      else {
        paramsList.add(docParameter);
      }
      Map<String, String> paramTagsComments = getParamTagsComments(methodDoc);
      String description = paramTagsComments.get(name);
      if (description == null) {
//        logger.warning("Missing description of parameter " + name + " of method " + methodDoc.name());
        description = "";
      }
      docParameter.setDescription(description);
    }
    return paramsList;
  }

  private List<DocParameter> generateQueryParameters(Parameter queryParameter) throws IntrospectionException,
          ClassNotFoundException {
    Collection<Tag> tags = newArrayList();
    return new QueryParamGenerator().createParamList(
            queryParameter,
            annotationReader.read(Arrays.asList(queryParameter.annotations()), tags).requestParamAnnotation().orNull());
  }

 /*
  * @return The parameters' comments.
  */
  private static Map<String, String> getParamTagsComments(final MethodDoc methodDoc) {
    Map<String, String> paramComments = new HashMap<String, String>();
    for (ParamTag paramTag : methodDoc.paramTags()) {
      paramComments.put(paramTag.parameterName(), paramTag.parameterComment());
    }
    return paramComments;
  }

  private static String paramAnnotationTypeString(List<AnnotationDesc> annotations) {
    List<String> types = newArrayList();
    for (AnnotationDesc annotationDesc : annotations) {
      types.add(annotationDesc.annotationType().simpleTypeName());
    }
    return Joiner.on(",").join(types);
  }
}

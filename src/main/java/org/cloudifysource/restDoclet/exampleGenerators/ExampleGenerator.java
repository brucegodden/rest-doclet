package org.cloudifysource.restDoclet.exampleGenerators;

import java.util.logging.Logger;

import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.generation.Utils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import com.sun.javadoc.*;


public class ExampleGenerator {
  private static final Logger LOGGER = Logger.getLogger(ExampleGenerator.class.getName());

  private final ObjectCreator objectCreator_;

  public ExampleGenerator(ObjectCreator objectCreator) {
    objectCreator_ = objectCreator;
  }

  public DocJsonResponseExample exampleResponse(final MethodDoc methodDoc) throws Exception {
    try {
      Object newInstance = createObjectFromType(methodDoc.returnType());

      String generateExample = new ObjectMapper()
              .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
              .writeValueAsString(newInstance);

      return new DocJsonResponseExample(Utils.getIndentJson(generateExample), "");
    } catch (Exception e) {
      throw new Exception("Failed to create response example for " + methodDoc.qualifiedName(), e);
    }
  }

  public DocJsonRequestExample exampleRequest(final MethodDoc methodDoc) throws Exception {
    try {
      final Parameter requestBodyParameter = getRequestBodyParameter(methodDoc);
      if (requestBodyParameter == null) {
        return DocJsonRequestExample.EMPTY;
      }

      final Object newInstance = createObjectFromType(requestBodyParameter.type());
      final String generateExample = new ObjectMapper()
              .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
              .writeValueAsString(newInstance);

      return new DocJsonRequestExample(Utils.getIndentJson(generateExample), "");
    }
    catch (Exception e) {
      throw new Exception("Failed to create request example for " + methodDoc.qualifiedName(), e);
    }
  }

  private Parameter getRequestBodyParameter(final MethodDoc methodDoc) {
    for (final Parameter param : methodDoc.parameters()) {
      for (final AnnotationDesc annotationDesc : param.annotations()) {
        if (annotationDesc.annotationType().simpleTypeName().contains("RequestBody")) {
          return param;
        }
      }
    }
    return null;
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
}

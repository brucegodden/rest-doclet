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

  private final RequestObjectCreator requestObjectCreator_;
  private final ResponseObjectCreator responseObjectCreator_;

  public ExampleGenerator(RequestObjectCreator requestObjectCreator, ResponseObjectCreator responseObjectCreator) {
    requestObjectCreator_ = requestObjectCreator;
    responseObjectCreator_ = responseObjectCreator;
  }

  public DocJsonResponseExample exampleResponse(final MethodDoc methodDoc) throws Exception {
    try {
      Type type = methodDoc.returnType();
      try {
        Object object = responseObjectCreator_.createObject(new ObjectType(type));

        String generateExample = new ObjectMapper()
                .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
                .writeValueAsString(object);

        return new DocJsonResponseExample(Utils.getIndentJson(generateExample), "");
      }
      catch (Exception e) {
        LOGGER.severe("FAILED processing response from '" + methodDoc.name() + "' of type " + type.simpleTypeName());
        throw e;
      }
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

      Type type = requestBodyParameter.type();
      try {
        final Object object = requestObjectCreator_.createObject(new ObjectType(type));

        final String generateExample = new ObjectMapper()
            .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
            .writeValueAsString(object);

        return new DocJsonRequestExample(Utils.getIndentJson(generateExample), "");
      }
      catch (Exception e) {
        LOGGER.severe("FAILED processing parameter '" + requestBodyParameter.name() + "' of type " + type.simpleTypeName());
        throw e;
      }
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
}

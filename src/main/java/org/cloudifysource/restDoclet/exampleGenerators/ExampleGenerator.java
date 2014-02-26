package org.cloudifysource.restDoclet.exampleGenerators;

import java.io.IOException;
import java.io.StringWriter;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.generation.Utils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

/**
 * @author edward
 */
public class ExampleGenerator {

  private final ObjectCreator objectCreator_;

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
//      logger.warning("Could not generate request example for method: " + httpMethod.getMethodSignatureName()
//                     + " with the request parameter type " + clazz.getName()
//                     + ". Exception was: " + e);
//      generateExample = RestDocConstants.FAILED_TO_CREATE_REQUEST_EXAMPLE + "."
//                        + LINE_SEPARATOR
//                        + "Parameter type: " + clazz.getName() + "."
//                        + LINE_SEPARATOR
//                        + "The exception caught was " + e;
      return new DocJsonResponseExample(RestDocConstants.FAILED_TO_CREATE_REQUEST_EXAMPLE + "."
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
}

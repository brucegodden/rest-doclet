package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.collect.Maps.newHashMap;

public class RequestObjectCreator extends ObjectCreator {

  @Override
  protected Map<String, ObjectType> getProperties(final Class cls) {
    final Map<String, ObjectType> properties = newHashMap();

    // Look for a constructor with JsonProperty annotations. (There can only be one.)
    for (Constructor c : cls.getConstructors()) {
      Type[] types = c.getGenericParameterTypes();
      Annotation[][] annotations = c.getParameterAnnotations();

      for (int i = 0; i < types.length; i++) {
        JsonProperty annotation = (JsonProperty) findAnnotation(JsonProperty.class, annotations[i]);
        if (annotation != null && StringUtils.isNotEmpty(annotation.value())) {
          properties.put(uncapitalize(annotation.value()), new ObjectType(types[i]));
        }
      }

      if (!properties.isEmpty()) {
        break;
      }
    }

    // If we didn't find an annotated constructor then look for setters.
    if (properties.isEmpty()) {
      for (Method m : cls.getMethods()) {
        if (Modifier.isPublic(m.getModifiers())
            && m.getName().startsWith("set")
            && m.getName().length() > 3
            && m.getParameterTypes().length == 1
            && !OBJECT_METHODS.contains(m.getName())) {
          properties.put(uncapitalize(m.getName().substring(3)), new ObjectType(m.getGenericParameterTypes()[0]));
        }
      }
    }

    return properties;
  }

  private Annotation findAnnotation(final Class annotationClass, final Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotationClass.isAssignableFrom(annotation.getClass())) {
        return annotation;
      }
    }
    return null;
  }
}

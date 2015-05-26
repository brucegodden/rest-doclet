package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.collect.Maps.newHashMap;

public class ResponseObjectCreator extends ObjectCreator {

  private static final Logger LOGGER = Logger.getLogger(ResponseObjectCreator.class.getName());

  private static final String[] PREFIXES = {"get", "is"};

  @Override
  protected Map<String, Type> getProperties(final Class cls) {
    final Map<String, Type> properties = newHashMap();

    for (Method m : cls.getMethods()) {
      if (Modifier.isPublic(m.getModifiers()) && m.getParameterTypes().length == 0 && !OBJECT_METHODS.contains(m.getName())) {
        for (String prefix : PREFIXES) {
          if (m.getName().startsWith(prefix) && m.getName().length() > prefix.length()) {
            properties.put(uncapitalize(m.getName().substring(prefix.length())), m.getGenericReturnType());
            break;
          }
        }
      }
    }

    return properties;
  }
}

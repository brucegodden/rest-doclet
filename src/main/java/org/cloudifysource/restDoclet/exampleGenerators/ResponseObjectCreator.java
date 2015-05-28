package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.logging.Logger;

import org.cloudifysource.restDoclet.annotations.JsonResponseExample;
import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.collect.Maps.newHashMap;

public class ResponseObjectCreator extends ObjectCreator {

  private static final Logger LOGGER = Logger.getLogger(ResponseObjectCreator.class.getName());

  private static final String[] PREFIXES = {"get", "is"};

  public ResponseObjectCreator() {
    super(responseExampleCreator_);
  }

  @Override
  protected Map<String, ObjectType> getProperties(final Class cls) {
    final Map<String, ObjectType> properties = newHashMap();

    for (Method m : cls.getMethods()) {
      if (Modifier.isPublic(m.getModifiers()) && m.getParameterTypes().length == 0 && !OBJECT_METHODS.contains(m.getName())) {
        for (String prefix : PREFIXES) {
          if (m.getName().startsWith(prefix) && m.getName().length() > prefix.length()) {
            properties.put(uncapitalize(m.getName().substring(prefix.length())), new ObjectType(m.getGenericReturnType()));
            break;
          }
        }
      }
    }

    return properties;
  }

  private static ExampleCreator responseExampleCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return cls.getAnnotation(JsonResponseExample.class) != null;
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      final JsonResponseExample annotation = (JsonResponseExample) cls.getAnnotation(JsonResponseExample.class);
      final String body = annotation.responseBody().trim();
      final Object example = new ObjectMapper().readTree(body);
      return example;
    }
  };
}

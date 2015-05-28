package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Primitives;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import net.sf.cglib.beans.BeanGenerator;

public abstract class ObjectCreator {

  private static final Logger LOGGER = Logger.getLogger(ObjectCreator.class.getName());

  protected static final Set<String> OBJECT_METHODS = newHashSet();
  static {
    for (Method m : Object.class.getMethods()) {
      OBJECT_METHODS.add(m.getName());
    }
  }

  private List<ExampleCreator> exampleCreators_;

  public static String capitalize(final String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public static String uncapitalize(final String name) {
    return name.substring(0, 1).toLowerCase() + name.substring(1);
  }

  public ObjectCreator() {
    exampleCreators_ = newArrayList(primitiveCreator_, wrapperCreator_, stringCreator_, enumCreator_,
        dateCreator_, calendarCreator_, arrayCreator_, listCreator_, setCreator_, mapCreator_);
  }

  public Object createObject(final ObjectType objectType) throws Exception {
    final Class clazz = Class.forName(objectType.getQualifiedName());

    for (ExampleCreator creator : exampleCreators_) {
      if (creator.match(clazz)) {
        return creator.create(clazz, objectType);
      }
    }

    final Map<String, ObjectType> properties = getProperties(clazz);

    final BeanGenerator beanGenerator = new BeanGenerator();
    for (String name : properties.keySet()) {
      beanGenerator.addProperty(name, Object.class);
    }

    final Object object = beanGenerator.create();
    for (String name : properties.keySet()) {
      final ObjectType propertyType = properties.get(name);
      try {
        if (!propertyType.getQualifiedName().equals(objectType.getQualifiedName())) { // Avoid infinite recursion
          final Method method = object.getClass().getMethod("set" + capitalize(name), Object.class);
          method.invoke(object, createObject(propertyType));
        }
      }
      catch (Exception e) {
        LOGGER.severe("FAILED processing field '" + name + "' of type " + propertyType.toString());
        throw e;
      }
      catch (Error e) {
        LOGGER.severe("FAILED processing field '" + name + "' of type " + propertyType.toString());
        throw e;
      }
    }

    return object;
  }

  protected abstract Map<String, ObjectType> getProperties(final Class cls);

  interface ExampleCreator {
    boolean match(Class cls);
    Object create(Class cls, ObjectType type) throws Exception;
  }

  private static ExampleCreator stringCreator_ = new ExampleCreator() {

    private List<String> examples_ = newArrayList("foo", "bar", "quux");

    @Override
    public boolean match(Class cls) {
      return String.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) {
      Collections.rotate(examples_, 1);
      return examples_.get(0);
    }
  };

  private static ExampleCreator primitiveCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return cls.isPrimitive();
    }

    @Override
    public Object create(final Class cls, final ObjectType type) {
      return PrimitiveExampleValues.getValue(cls);
    }
  };

  private static ExampleCreator wrapperCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Primitives.isWrapperType(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      return PrimitiveExampleValues.getValue(Primitives.unwrap(cls));
    }
  };

  private static ExampleCreator enumCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return cls.isEnum();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final ObjectType type) {
      final Object[] constants = cls.getEnumConstants();
      return constants[constants.length / 2];
    }
  };

  private ExampleCreator dateCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Date.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      return new Date();
    }
  };

  private ExampleCreator calendarCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Calendar.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      return Calendar.getInstance();
    }
  };

  private ExampleCreator arrayCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return cls.isArray();
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      final Class componentClass = cls.getComponentType();
      Object array = Array.newInstance(Object.class, 1);
      Array.set(array, 0, createObject(new ObjectType(componentClass)));
      return array;
    }
  };

  private ExampleCreator listCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return List.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      ObjectType contentType;

      if (type.isParameterized()) {
        contentType = type.getParameterTypes()[0];
      }
      else {
        contentType = new ObjectType(Object.class);
      }

      return ImmutableList.of(createObject(contentType));
    }
  };

  private ExampleCreator setCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Set.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      ObjectType contentType;

      if (type.isParameterized()) {
        contentType = type.getParameterTypes()[0];
      }
      else {
        contentType = new ObjectType(Object.class);
      }

      return ImmutableSet.of(createObject(contentType));
    }
  };

  private ExampleCreator mapCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Map.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls, final ObjectType type) throws Exception {
      ObjectType keyType, valueType;

      if (type.isParameterized()) {
        keyType = type.getParameterTypes()[0];
        valueType = type.getParameterTypes()[1];
      }
      else {
        keyType = valueType = new ObjectType(String.class);
      }

      return ImmutableMap.of(createObject(keyType), createObject(valueType));
    }
  };
}

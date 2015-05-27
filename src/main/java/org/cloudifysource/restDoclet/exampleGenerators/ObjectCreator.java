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
  private List<ParametricExampleCreator> paramExampleCreators_;

  public static String capitalize(final String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public static String uncapitalize(final String name) {
    return name.substring(0, 1).toLowerCase() + name.substring(1);
  }

  public ObjectCreator() {
    exampleCreators_ = newArrayList(primitiveCreator_, wrapperCreator_, stringCreator_, enumCreator_,
        dateCreator_, calendarCreator_, arrayCreator_, listCreator_, setCreator_, mapCreator_);

    paramExampleCreators_ = newArrayList(listCreator_, setCreator_, mapCreator_);
  }

  public Object createObject(final ObjectType objectType) throws Exception {
    final Class clazz = Class.forName(objectType.getQualifiedName());

    if (objectType.isParameterized()) {
      for (ParametricExampleCreator creator : paramExampleCreators_) {
        if (creator.match(clazz)) {
          return creator.create(clazz, objectType.getParameterTypes());
        }
      }
    }

    for (ExampleCreator creator : exampleCreators_) {
      if (creator.match(clazz)) {
        return creator.create(clazz);
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
    Object create(Class cls) throws Exception;
  }

  interface ParametricExampleCreator extends ExampleCreator {
    Object create(Class cls, ObjectType[] parameters) throws Exception;
  }

  private static ExampleCreator stringCreator_ = new ExampleCreator() {

    private List<String> examples_ = newArrayList("foo", "bar", "quux");
    @Override
    public boolean match(Class cls) {
      return String.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) {
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
    public Object create(final Class cls) {
      return PrimitiveExampleValues.getValue(cls);
    }
  };

  private static ExampleCreator wrapperCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Primitives.isWrapperType(cls);
    }

    @Override
    public Object create(final Class cls) throws Exception {
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
    public Object create(final Class cls) {
      return cls.getEnumConstants()[0];
    }
  };

  private ExampleCreator dateCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Date.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws Exception {
      return new Date();
    }
  };

  private ExampleCreator calendarCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Calendar.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws Exception {
      return Calendar.getInstance();
    }
  };

  private ExampleCreator arrayCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return cls.isArray();
    }

    @Override
    public Object create(final Class cls) throws Exception {
      final Class componentClass = cls.getComponentType();
      Object array = Array.newInstance(Object.class, 1);
      Array.set(array, 0, createObject(new ObjectType(componentClass)));
      return array;
    }
  };

  private ParametricExampleCreator listCreator_ = new ParametricExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return List.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws Exception {
      return this.create(cls, new ObjectType[] {new ObjectType(Object.class)});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final ObjectType[] parameters) throws Exception {
      List list = ImmutableList.of(createObject(parameters[0]));
      return list;
    }
  };

  private ParametricExampleCreator setCreator_ = new ParametricExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Set.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws Exception {
      return this.create(cls, new ObjectType[] {new ObjectType(Object.class)});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final ObjectType[] parameters) throws Exception {
      Set set = ImmutableSet.of(createObject(parameters[0]));
      return set;
    }
  };

  private ParametricExampleCreator mapCreator_ = new ParametricExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Map.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws Exception {
      return this.create(cls, new ObjectType[]{new ObjectType(String.class), new ObjectType(String.class)});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final ObjectType[] parameters) throws Exception {
      Map map = ImmutableMap.of(createObject(parameters[0]), createObject(parameters[1]));
      return map;
    }
  };
}

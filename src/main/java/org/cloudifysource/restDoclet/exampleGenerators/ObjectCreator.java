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

  public Object createObject(final Class cls) throws Exception {
    for (ExampleCreator creator : exampleCreators_) {
      if (creator.match(cls)) {
        return creator.create(cls);
      }
    }

    final Map<String, Type> properties = getProperties(cls);

    final BeanCreator beanCreator = new BeanCreator();
    for (String name : properties.keySet()) {
      beanCreator.addProperty(name, Object.class);
    }

    final Object object = beanCreator.create(this);
    for (String name : properties.keySet()) {
      final Type type = properties.get(name);
      try {
        if (!type.equals(cls)) { // Avoid infinite recursion
          final Method method = object.getClass().getMethod("set" + capitalize(name), Object.class);
          method.invoke(object, createObjectFromType(type));
        }
      }
      catch (Exception e) {
        LOGGER.severe("FAILED processing field '" + name + "' of type " + type.getTypeName());
        throw e;
      }
      catch (Error e) {
        LOGGER.severe("FAILED processing field '" + name + "' of type " + type.getTypeName());
        throw e;
      }
    }

    return object;
  }

  protected abstract Map<String, Type> getProperties(final Class cls);

  public Object createParameterizedObject(final Class base, final Class[] paramClasses) throws Exception {
    for (ParametricExampleCreator creator : paramExampleCreators_) {
      if (creator.match(base)) {
        return creator.create(base, paramClasses);
      }
    }
    return createObject(base);
  }

  protected Object createObjectFromType(final Type type) throws Exception {
    if (type instanceof ParameterizedType) {
      final ParameterizedType pType = (ParameterizedType) type;
      return createParameterizedObject((Class) pType.getRawType(), reflectTypesToClasses(pType.getActualTypeArguments()));
    }
    else {
      return createObject((Class) type);
    }
  }

  private Class[] reflectTypesToClasses(Type[] types) throws ClassNotFoundException {
    Class[] classes = new Class[types.length];
    for (int i = 0; i < types.length; i++) {
      final Type type = types[i];

      if (type instanceof Class) {
        classes[i] = (Class) type;
        continue;
      }

      if (type instanceof WildcardType) {
        WildcardType wType = (WildcardType) type;
        final Type upperType = wType.getUpperBounds()[0];
        if (upperType instanceof Class) {
          final Class upperClass = (Class) upperType;
          if (!upperClass.equals(Object.class)) {
            classes[i] = upperClass;
            continue;
          }
        }
      }

      throw new ClassNotFoundException("Unable to find class for type " + type.getTypeName());
    }
    return classes;
  }

  interface ExampleCreator {
    boolean match(Class cls);
    Object create(Class cls) throws Exception;
  }

  interface ParametricExampleCreator extends ExampleCreator {
    Object create(Class cls, Class[] paramClasses) throws Exception;
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
      Array.set(array, 0, createObject(componentClass));
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
      return this.create(cls, new Class[]{Object.class});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final Class[] paramClasses) throws Exception {
      List list = ImmutableList.of(createObject(paramClasses[0]));
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
      return this.create(cls, new Class[]{Object.class});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final Class[] paramClasses) throws Exception {
      Set set = ImmutableSet.of(createObject(paramClasses[0]));
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
      return this.create(cls, new Class[]{String.class, String.class});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final Class[] paramClasses) throws Exception {
      Map map = ImmutableMap.of(createObject(paramClasses[0]), createObject(paramClasses[1]));
      return map;
    }
  };
}

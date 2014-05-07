package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.google.common.primitives.Primitives;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Ed Kimber
 */
public class ObjectCreator {

  private Objenesis objenesis_;
  private static final Logger logger_ = Logger.getLogger(ObjectCreator.class.getName());
  private List<ExampleCreator> exampleCreators_;
  private List<ParametricExampleCreator> paramExampleCreators_;


  public ObjectCreator() {
    objenesis_ = new ObjenesisStd();
    exampleCreators_ = newArrayList(primitiveCreator_, wrapperCreator_, stringCreator_, enumCreator_, dateCreator_, listCreator_, mapCreator_);
    paramExampleCreators_ = newArrayList(listCreator_, mapCreator_);
  }

  public Object createObject(final Class<?> cls) throws IllegalAccessException {
    for (ExampleCreator creator : exampleCreators_) {
      if (creator.match(cls)) {
        return creator.create(cls);
      }
    }

    if (isAbstractOrInterface(cls)) return createProxy(cls);

    try {
      Object object = objenesis_.newInstance(cls);
      instantiateFieldsOn(object);
      return object;
    }
    catch (IllegalAccessError illegal) {
      logger_.warning("Could not instantiate an object of class: " + cls.getName());
      return null;
    }
  }

  public Object createParameterizedObject(final Class base, final Class[] paramClasses) throws IllegalAccessException {
    for (ParametricExampleCreator creator : paramExampleCreators_) {
      if (creator.match(base)) {
        return creator.create(base, paramClasses);
      }
    }
    return createObject(base);
  }

  public void instantiateFieldsOn(final Object object) throws IllegalAccessException {
    Class<?> cls = object.getClass();
    for (Field f : cls.getDeclaredFields()) {
      if (f.getType().equals(cls)) {
        continue; //avoid infinite recursion!
      }
      else {
        tryToSetField(f, object);
      }
    }
  }

  private void tryToSetField(final Field field, final Object object) {
    try {
      field.setAccessible(true);
      Class<?> fieldType = field.getType();
      if (field.getGenericType() instanceof ParameterizedType) {
        field.set(object, createParameterizedType(fieldType, (ParameterizedType) field.getGenericType()));
      }
      else {
        field.set(object, createObject(fieldType));
      }
    }
    catch (IllegalAccessException illegal) {
      // serialVersionUID is normally defined as private static final and generates an IllegalAccessException
      // but it isn't actually a field that we would ever expect to set so ignore the error.
      if (!field.getName().equals("serialVersionUID")) {
        logger_.warning("Could not set field " + field.getName() + " on a " + object.getClass());
      }
    }
    catch (ClassNotFoundException e) {
      logger_.warning("Could not set field " + field.getName() + " on a " + object.getClass());
    }
  }

  public Object createProxy(final Class cls) {
    return Enhancer.create(cls, new MethodInterceptor() {
      @Override
      public Object intercept(final Object proxy, final Method method,
                              final Object[] args, final MethodProxy methodProxy)
              throws Throwable
      {
        return createObject(method.getReturnType());
      }
    });
  }

  private Object createParameterizedType(final Class base, final ParameterizedType genericType)
          throws IllegalAccessException, ClassNotFoundException
  {
    return createParameterizedObject(base, reflectTypesToClasses(genericType.getActualTypeArguments()));
  }

  Class[] reflectTypesToClasses(Type[] types) throws ClassNotFoundException {
    Class[] classes = new Class[types.length];
    for (int i = 0; i < types.length; i++) {
      if (types[i] instanceof Class) {
        classes[i] = (Class) (types[i]);
      }
      else {
        logger_.warning("nonclass: " + types[i]);
        classes[i] = String.class;
      }
    }
    return classes;
  }

  private boolean isAbstractOrInterface(final Class<?> cls) {
    int modifiers = cls.getModifiers();
    return Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers);
  }

  interface ExampleCreator {
    boolean match(Class cls);
    Object create(Class cls) throws IllegalAccessException;
  }

  interface ParametricExampleCreator extends ExampleCreator {
    Object create(Class cls, Class[] paramClasses) throws IllegalAccessException;
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
    public Object create(final Class cls) throws IllegalAccessException {
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
      return Enum.valueOf((Class<? extends Enum>) cls, cls.getEnumConstants()[0].toString());
    }
  };

  private ExampleCreator dateCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Date.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws IllegalAccessException {
      return new Date();
    }
  };

  private ParametricExampleCreator listCreator_ = new ParametricExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return List.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws IllegalAccessException {
      return this.create(cls, new Class[]{Object.class});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final Class[] paramClasses) throws IllegalAccessException {
        LinkedList list = new LinkedList();
        list.add(createObject(paramClasses[0]));
        return list;
    }
  };

  private ParametricExampleCreator mapCreator_ = new ParametricExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Map.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws IllegalAccessException {
      return this.create(cls, new Class[] {String.class, String.class});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(final Class cls, final Class[] paramClasses) throws IllegalAccessException {
      HashMap map = new HashMap();
      map.put(createObject(paramClasses[0]), createObject(paramClasses[1]));
      return map;
    }
  };
}

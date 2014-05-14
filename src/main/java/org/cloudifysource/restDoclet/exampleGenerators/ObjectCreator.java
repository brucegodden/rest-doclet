package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.lang.StringUtils;
import org.cloudifysource.restDoclet.annotations.DocumentCommand;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.google.common.primitives.Primitives;
import sun.reflect.generics.tree.Wildcard;

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
    exampleCreators_ = newArrayList(primitiveCreator_, wrapperCreator_, stringCreator_, enumCreator_, dateCreator_,
            arrayCreator_, listCreator_, mapCreator_, commandCreator_, failureCreator_);
    paramExampleCreators_ = newArrayList(listCreator_, mapCreator_);
  }

  public Object createObject(final Class<?> cls) throws IllegalAccessException {
    for (ExampleCreator creator : exampleCreators_) {
      if (creator.match(cls)) {
        return creator.create(cls);
      }
    }

    if (isAbstractOrInterface(cls)) {
      return createProxy(cls);
    }

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
    for (Class<?> cls = object.getClass(); cls != null; cls = cls.getSuperclass()) {
      for (Field f : cls.getDeclaredFields()) {
        f.setAccessible(true);
        if (f.getType().equals(cls)) {
          continue; //avoid infinite recursion!
        } else if (!f.getType().isPrimitive() && f.get(object) != null) {
          continue; // Ignore non-primitive fields that already have a value.
        } else {
          tryToSetField(f, object);
        }
      }
    }
  }

  private void tryToSetField(final Field field, final Object object) {
    try {
      final int modifiers = field.getModifiers();
      if (!Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers)) {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        if (field.getGenericType() instanceof ParameterizedType) {
          field.set(object, createParameterizedType(fieldType, (ParameterizedType) field.getGenericType()));
        } else {
          field.set(object, createObject(fieldType));
        }
      }
    }
    catch (IllegalAccessException illegal) {
      logger_.warning("Could not set field " + field.getName() + " on a " + object.getClass());
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

      logger_.warning("nonclass: " + type);
      classes[i] = Failure.class;
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
      return cls.getEnumConstants()[0];
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

  private ExampleCreator arrayCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return cls.isArray();
    }

    @Override
    public Object create(final Class cls) throws IllegalAccessException {
      final Class componentClass = cls.getComponentType();
      Object array = Array.newInstance(componentClass, 1);
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

  private ExampleCreator commandCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return findAnnotation(DocumentCommand.class, cls.getDeclaredAnnotations()) != null;
    }

    @Override
    public Object create(final Class cls) throws IllegalAccessException {
      BeanCreator beanCreator = new BeanCreator();
      boolean documented = false;

      // Look for a constructor with the JsonProperty annotation. (There can only be one.)
      for (Constructor c : cls.getConstructors()) {
        Type[] types = c.getGenericParameterTypes();
        Annotation[][] annotations = c.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
          JsonProperty annotation = (JsonProperty) findAnnotation(JsonProperty.class, annotations[i]);
          if (annotation != null && StringUtils.isNotEmpty(annotation.value())) {
            beanCreator.addProperty(annotation.value(), types[i]);
            documented = true;
          }
        }
        if (documented) {
          break;
        }
      }

      // If we didn't find an annotated constructor then look for setters and create
      // equivalent getters.
      if (!documented) {
        // Although we would like to do this for the annotated constructor case as well it
        // causes construction of the object to fail because the constructor doesn't get
        // called. So we only do it for the setters case.
        beanCreator.setSuperclass(cls);

        for (Method m : cls.getMethods()) {
          if (m.getName().startsWith("set") && m.getName().length() > 3 && m.getParameterTypes().length == 1) {
            String name = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
            beanCreator.addProperty(name, m.getGenericParameterTypes()[0]);
          }
        }
      }

      Object bean = beanCreator.create(ObjectCreator.this);
      instantiateFieldsOn(bean);
      return bean;
    }

    private Annotation findAnnotation(final Class annotationClass, final Annotation[] annotations) {
      for (Annotation annotation : annotations) {
        if (annotationClass.isAssignableFrom(annotation.getClass())) {
          return annotation;
        }
      }
      return null;
    }
  };

  private class Failure {
  }

  private ExampleCreator failureCreator_ = new ExampleCreator() {
    @Override
    public boolean match(final Class cls) {
      return Failure.class.isAssignableFrom(cls);
    }

    @Override
    public Object create(final Class cls) throws IllegalAccessException {
      return null;
    }
  };
}

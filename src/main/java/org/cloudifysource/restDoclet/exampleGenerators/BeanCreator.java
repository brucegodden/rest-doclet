package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.cglib.beans.BeanGenerator;

import static com.google.common.collect.Maps.newHashMap;

/**
 * @author bruce
 */
public class BeanCreator {

  private static final Logger logger_ = Logger.getLogger(BeanCreator.class.getName());

  private Map<String, Class> mapProperties;
  private BeanGenerator beanGenerator;

  public BeanCreator() {
    beanGenerator = new BeanGenerator();
    mapProperties = newHashMap();
  }

  public void setSuperclass(final Class cls) {
    beanGenerator.setSuperclass(cls);
  }

  public void addProperty(final String name, final Type type) {
    if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      Type[] types = pType.getActualTypeArguments();
      Type rawType = pType.getRawType();
      Class rawClass = (Class) rawType;
      switch (types.length) {
        case 0:
          beanGenerator.addProperty(name, rawClass);
          return;

        case 1:
          if (List.class.isAssignableFrom(rawClass) || rawType instanceof GenericArrayType) {
            Object array = Array.newInstance((Class) types[0], 0);
            beanGenerator.addProperty(name, array.getClass());
            return;
          }
          break;

        case 2:
          if (types[0] instanceof Class
              && types[1] instanceof Class
              && Map.class.isAssignableFrom(rawClass)
              && String.class.isAssignableFrom((Class) types[0])) {
            beanGenerator.addProperty(name, Map.class);
            mapProperties.put(name, (Class) types[1]);
            return;
          }

        default:
          break;
      }
    }

    if (type instanceof Class) {
      beanGenerator.addProperty(name, (Class) type);
      return;
    }

    logger_.warning("Unable to generate getter for '" + name + "' field deduced from constructor/setter");
  }

  public Object create(final ObjectCreator creator) throws IllegalAccessException {
    final Object bean = beanGenerator.create();

    for (Map.Entry<String, Class> entry : mapProperties.entrySet()) {
      try {
        String setterName = "set" + entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
        Method setter = bean.getClass().getDeclaredMethod(setterName, Map.class);
        final Map map = newHashMap();
        map.put(creator.createObject(String.class), creator.createObject(entry.getValue()));
        setter.invoke(bean, map);
      } catch (NoSuchMethodException e) {
        // Impossible!
      } catch (InvocationTargetException e) {
        // Impossible!
      }
    }

    return bean;
  }
}

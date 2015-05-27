package org.cloudifysource.restDoclet.exampleGenerators;

import java.util.List;
import java.util.Map;

import com.sun.javadoc.ClassDoc;

import static com.google.common.collect.Maps.newHashMap;

public class ObjectType {

  private final static Map<Class, Class> PRIMITIVE_WRAPPERS = newHashMap();
  static {
    PRIMITIVE_WRAPPERS.put(byte.class, Byte.class);
    PRIMITIVE_WRAPPERS.put(short.class, Short.class);
    PRIMITIVE_WRAPPERS.put(int.class, Integer.class);
    PRIMITIVE_WRAPPERS.put(long.class, Long.class);
    PRIMITIVE_WRAPPERS.put(float.class, Float.class);
    PRIMITIVE_WRAPPERS.put(double.class, Double.class);
    PRIMITIVE_WRAPPERS.put(boolean.class, Boolean.class);
    PRIMITIVE_WRAPPERS.put(char.class, Character.class);
  }

  private String qualifiedName_;
  private ObjectType[] parameterTypes_;

  public ObjectType(final com.sun.javadoc.Type type) {
    qualifiedName_ = qualifiedNameFromJavadocType(type);

    if (type.asParameterizedType() != null) {
      final com.sun.javadoc.Type[] paramTypes = type.asParameterizedType().typeArguments();
      parameterTypes_ = new ObjectType[paramTypes.length];
      for (int i = 0; i < paramTypes.length; i++) {
        parameterTypes_[i] = new ObjectType(paramTypes[i]);
      }
    }
  }

  public ObjectType(final java.lang.reflect.Type type) {
    if (type instanceof java.lang.reflect.ParameterizedType) {
      final java.lang.reflect.ParameterizedType pType = (java.lang.reflect.ParameterizedType) type;
      qualifiedName_ = pType.getRawType().getTypeName();

      final java.lang.reflect.Type[] paramTypes = pType.getActualTypeArguments();
      parameterTypes_ = new ObjectType[paramTypes.length];
      for (int i = 0; i < paramTypes.length; i++) {
        parameterTypes_[i] = new ObjectType(paramTypes[i]);
      }
    }
    else if (type instanceof java.lang.reflect.WildcardType) {
      final java.lang.reflect.WildcardType wType = (java.lang.reflect.WildcardType) type;
      qualifiedName_ = wType.getUpperBounds()[0].getTypeName();
    }
    else {
      final Class clazz = (Class) type;
      if (clazz.isPrimitive()) {
        qualifiedName_ = PRIMITIVE_WRAPPERS.get(clazz).getName();
      }
      else if (clazz.isArray()) {
        qualifiedName_ = List.class.getTypeName();
        parameterTypes_ = new ObjectType[] {new ObjectType(clazz.getComponentType())};
      }
      else {
        qualifiedName_ = type.getTypeName();
      }
    }
  }

  public String getQualifiedName() {
    return qualifiedName_;
  }

  public boolean isParameterized() {
    return parameterTypes_ != null;
  }

  public ObjectType[] getParameterTypes() {
    return parameterTypes_;
  }

  private String qualifiedNameFromJavadocType(com.sun.javadoc.Type type) {
    String qualifiedName;

    if (type.asClassDoc() != null) {
      ClassDoc cd = type.asClassDoc();
      ClassDoc outer = cd.containingClass();
      if (outer == null) {
        qualifiedName = cd.qualifiedName();
      }
      else {
        String simpleName = cd.name();
        simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);
        qualifiedName = outer.qualifiedName() + '$' + simpleName;
      }
    }
    else {
      qualifiedName = type.qualifiedTypeName();
    }

    return qualifiedName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(qualifiedName_);
    if (parameterTypes_ != null) {
      char prefix = '<';
      for (ObjectType type : parameterTypes_) {
        builder.append(prefix);
        builder.append(type.toString());
        prefix = ',';
      }
      builder.append('>');
    }
    return builder.toString();
  }
}

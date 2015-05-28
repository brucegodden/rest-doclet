package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ObjectTypeTest {

  @Test
  public void primitiveTypeBecomesWrapper() throws Exception {
    final ObjectType type = new ObjectType(getField("primitive"));

    assertThat(type.getQualifiedName(), is(Character.class.getName()));
    assertThat(type.getParameterTypes(), nullValue());
  }

  @Test
  public void simpleClass() throws Exception {
    final ObjectType type = new ObjectType(getField("string"));

    assertThat(type.getQualifiedName(), is(String.class.getName()));
    assertThat(type.getParameterTypes(), nullValue());
  }

  @Test
  public void arrayClass() throws Exception {
    final ObjectType type = new ObjectType(getField("array"));

    assertThat(type.getQualifiedName(), is(List.class.getName()));
    assertThat(type.getParameterTypes(), notNullValue());
    assertThat(type.getParameterTypes().length, is(1));

    assertThat(type.getParameterTypes()[0].getQualifiedName(), is(Integer.class.getName()));
    assertThat(type.getParameterTypes()[0].getParameterTypes(), nullValue());
  }

  @Test
  public void parameterizedClass() throws Exception {
    final ObjectType type = new ObjectType(getField("list"));

    assertThat(type.getQualifiedName(), is(List.class.getName()));
    assertThat(type.getParameterTypes(), notNullValue());
    assertThat(type.getParameterTypes().length, is(1));

    assertThat(type.getParameterTypes()[0].getQualifiedName(), is(String.class.getName()));
    assertThat(type.getParameterTypes()[0].getParameterTypes(), nullValue());
  }

  @Test
  public void wildcardClass() throws Exception {
    final ObjectType type = new ObjectType(getField("wildcard"));

    assertThat(type.getQualifiedName(), is(Set.class.getName()));
    assertThat(type.getParameterTypes(), notNullValue());
    assertThat(type.getParameterTypes().length, is(1));

    assertThat(type.getParameterTypes()[0].getQualifiedName(), is(Byte.class.getName()));
    assertThat(type.getParameterTypes()[0].getParameterTypes(), nullValue());
  }

  @Test
  public void mapOfMaps() throws Exception {
    final ObjectType type = new ObjectType(getField("map"));

    assertThat(type.getQualifiedName(), is(Map.class.getName()));
    assertThat(type.getParameterTypes(), notNullValue());
    assertThat(type.getParameterTypes().length, is(2));

    assertThat(type.getParameterTypes()[0].getQualifiedName(), is(RestDocConstants.DocAnnotationTypes.class.getName()));
    assertThat(type.getParameterTypes()[0].getParameterTypes(), nullValue());

    assertThat(type.getParameterTypes()[1].getQualifiedName(), is(Map.class.getName()));
    assertThat(type.getParameterTypes()[1].getParameterTypes(), notNullValue());
    assertThat(type.getParameterTypes()[1].getParameterTypes().length, is(2));

    assertThat(type.getParameterTypes()[1].getParameterTypes()[0].getQualifiedName(), is(String.class.getName()));
    assertThat(type.getParameterTypes()[1].getParameterTypes()[0].getParameterTypes(), nullValue());

    assertThat(type.getParameterTypes()[1].getParameterTypes()[1].getQualifiedName(), is(Integer.class.getName()));
    assertThat(type.getParameterTypes()[1].getParameterTypes()[1].getParameterTypes(), nullValue());
  }

  private Type getField(final String name) throws NoSuchFieldException {
    final Class<Example> clazz = Example.class;
    final Field field = clazz.getDeclaredField(name);
    return field.getGenericType();
  }

  private static class Example {
    char primitive;
    String string;
    int[] array;
    List<String> list;
    Set<? extends Byte> wildcard;
    Map<RestDocConstants.DocAnnotationTypes, Map<String, Integer>> map;
  }
}

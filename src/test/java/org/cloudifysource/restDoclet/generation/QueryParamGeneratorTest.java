package org.cloudifysource.restDoclet.generation;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Set;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

/**
 * @author edward
 */
public class QueryParamGeneratorTest {
  @Test
  public void primitiveParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen =  new QueryParamGenerator();
    Parameter primitive = primitiveParam();

    List<DocParameter> params = gen.createParamList(primitive);
    assertThat(params, hasSize(1));
  }

  @Test
  public void wrapperParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator();
    Parameter wrapper = wrapperParam();

    List<DocParameter> params = gen.createParamList(wrapper);
    assertThat(params, hasSize(1));
  }

  @Test
  public void beanParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator();
    Parameter bean = beanParam();

    List<DocParameter> params = gen.createParamList(bean);
    assertThat(params, hasSize(2));
  }

  @Test
  public void listParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator();
    Parameter list = listParam();

    List<DocParameter> params = gen.createParamList(list);
    assertThat(params, hasSize(1));
  }

  @Test
  public void setParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator();
    Parameter set = setParam();

    List<DocParameter> params = gen.createParamList(set);
    assertThat(params, hasSize(1));
  }

  private Parameter setParam() {
    return mockParam(false, Set.class.getName());
  }

  private Parameter listParam() {
    return mockParam(false, List.class.getName());
  }

  private Parameter beanParam() {
    Parameter bean = mockParam(false, TestBean.class.getName());
    return bean;
  }

  private Parameter wrapperParam() {
    return mockParam(false, Long.class.getName());
  }

  private Parameter primitiveParam() {
    return mockParam(true, long.class.getName());
  }

  private Parameter mockParam(boolean primitive, String fqn) {
    Parameter parameter = mock(Parameter.class);
    Type type = mock(Type.class);
    when(type.isPrimitive()).thenReturn(primitive);
    when(type.qualifiedTypeName()).thenReturn(fqn);
    when(parameter.type()).thenReturn(type);
    return parameter;
  }

  static class TestBean {
    public Long getNumber() {
      return 1L;
    }

    public String getString() {
      return "foo";
    }
  }

}

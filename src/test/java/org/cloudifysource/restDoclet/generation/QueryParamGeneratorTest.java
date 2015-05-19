package org.cloudifysource.restDoclet.generation;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

import static com.google.common.collect.Maps.newHashMap;

/**
 * @author edward
 */
public class QueryParamGeneratorTest {
  public static final String PARAM_NAME = "limit";
  private static final String PARAM_DESCRIPTION = "Maximum number of objects to return";

  private DocRequestParamAnnotation annotation_;
  private QueryParamGenerator generator_;

  @Before
  public void setup() {
    Map<String, String> paramsDoc = newHashMap();
    paramsDoc.put(PARAM_NAME, PARAM_DESCRIPTION);

    final RestAnnotations restAnnotations = mock(RestAnnotations.class);
    when(restAnnotations.paramsDocumentation()).thenReturn(paramsDoc);
    generator_ = new QueryParamGenerator(restAnnotations);

    AnnotationDesc annotationDesc = mock(AnnotationDesc.class);
    when(annotationDesc.elementValues()).thenReturn(new AnnotationDesc.ElementValuePair[0]);
    annotation_ = new DocRequestParamAnnotation(annotationDesc);
  }

  @Test
  public void primitiveParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(primitiveParam(), annotation_);
    assertThat(param.getType(), is("long"));
  }

  @Test
  public void wrapperParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(wrapperParam(), annotation_);
    assertThat(param.getType(), is("Long"));
  }

  @Test
  public void listParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(listParam(), annotation_);
    assertThat(param.getType(), is("List<?>"));
  }

  @Test
  public void setParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(setParam(), annotation_);
    assertThat(param.getType(), is("Set<?>"));
  }

  @Test
  public void generalParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(beanParam(), annotation_);
    assertThat(param.getType(), is("org.cloudifysource.restDoclet.generation.QueryParamGeneratorTest$TestBean"));
  }

  @Test
  public void paramDescriptionGenerated() throws Exception {
    DocParameter param = generator_.createParam(primitiveParam(), annotation_);
    assertThat(param.getDescription(), is(PARAM_DESCRIPTION));
  }

  @Test
  public void primitiveParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    Parameter primitive = primitiveParam();

    List<DocParameter> params = generator_.createParamList(primitive, annotation_);
    assertThat(params, hasSize(1));
  }

  @Test
  public void wrapperParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    Parameter wrapper = wrapperParam();

    List<DocParameter> params = generator_.createParamList(wrapper, annotation_);
    assertThat(params, hasSize(1));
  }

  @Test
  public void beanParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    Parameter bean = beanParam();

    List<DocParameter> params = generator_.createParamList(bean, annotation_);
    assertThat(params, hasSize(2));
  }

  @Test
  public void listParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    Parameter list = listParam();

    List<DocParameter> params = generator_.createParamList(list, annotation_);
    assertThat(params, hasSize(1));
  }

  @Test
  public void setParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    Parameter set = setParam();

    List<DocParameter> params = generator_.createParamList(set, annotation_);
    assertThat(params, hasSize(1));
  }

  private Parameter setParam() {
    return mockParam(false, Set.class);
  }

  private Parameter listParam() {
    return mockParam(false, List.class);
  }

  private Parameter beanParam() {
    Parameter bean = mockParam(false, TestBean.class);
    return bean;
  }

  private Parameter wrapperParam() {
    return mockParam(false, Long.class);
  }

  private Parameter primitiveParam() {
    return mockParam(true, long.class);
  }

  private Parameter mockParam(boolean primitive, Class clazz) {
    Type type = mock(Type.class);
    when(type.isPrimitive()).thenReturn(primitive);
    when(type.simpleTypeName()).thenReturn(clazz.getSimpleName());
    when(type.qualifiedTypeName()).thenReturn(clazz.getName());

    Parameter parameter = mock(Parameter.class);
    when(parameter.type()).thenReturn(type);
    when(parameter.name()).thenReturn("limit");

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

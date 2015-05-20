package org.cloudifysource.restDoclet.generation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
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
    DocParameter param = generator_.createParam(primitiveParam(), annotation_, null);
    assertThat(param.getType(), is("long"));
  }

  @Test
  public void wrapperParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(wrapperParam(), annotation_, null);
    assertThat(param.getType(), is("Long"));
  }

  @Test
  public void listParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(listParam(), annotation_, null);
    assertThat(param.getType(), is("List&lt;String&gt;"));
  }

  @Test
  public void setParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(setParam(), annotation_, null);
    assertThat(param.getType(), is("Set&lt;?&gt;"));
  }

  @Test
  public void generalParamGenerated() throws Exception {
    DocParameter param = generator_.createParam(beanParam(), annotation_, null);
    assertThat(param.getType(), is("org.cloudifysource.restDoclet.generation.QueryParamGeneratorTest$TestBean"));
  }

  @Test
  public void paramDescriptionGenerated() throws Exception {
    DocParameter param = generator_.createParam(primitiveParam(), annotation_, null);
    assertThat(param.getDescription(), is(PARAM_DESCRIPTION));
  }

  @Test
  public void commandParamsGenerated() throws Exception {
    Parameter parameter = mock(Parameter.class);
    Type type = mock(Type.class);
    when(parameter.type()).thenReturn(type);
    when(type.qualifiedTypeName()).thenReturn(TestBean.class.getTypeName());

    List<DocParameter> params = generator_.createCommandParams(parameter);

    assertThat(params.size(), is(2));
    assertThat(params.get(0).getLogicalName(), is("number"));
    assertThat(params.get(0).getType(), is("Long"));
    assertThat(params.get(1).getLogicalName(), is("string"));
    assertThat(params.get(1).getType(), is("String"));
  }

  private Parameter setParam() {
    return mockParam(false, Set.class);
  }

  private Parameter listParam() {
    final ParameterizedType pType = mock(ParameterizedType.class);
    final Type type = mock(Type.class);
    when(pType.typeArguments()).thenReturn(new Type[] {type});
    when(type.qualifiedTypeName()).thenReturn(String.class.getTypeName());
    return mockParam(false, List.class, pType);
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
    return mockParam(primitive, clazz, mock(Type.class));
  }

  private Parameter mockParam(boolean primitive, Class clazz, Type type) {
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

package org.cloudifysource.restDoclet.generation;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Set;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

/**
 * @author edward
 */
public class QueryParamGeneratorTest {
  DocParameterGenerator docParameterGenerator_;

  @Before
  public void setup() {
    docParameterGenerator_ = mock(DocParameterGenerator.class);
  }

  @Test
  public void primitiveParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen =  new QueryParamGenerator(docParameterGenerator_);
    Parameter primitive = primitiveParam();

    List<DocParameter> params = gen.createParamList(primitive);
    assertThat(params, hasSize(1));
  }

  @Test
  public void wrapperParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator(docParameterGenerator_);
    Parameter wrapper = wrapperParam();

    List<DocParameter> params = gen.createParamList(wrapper);
    assertThat(params, hasSize(1));
  }

  @Test
  public void beanParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator(docParameterGenerator_);
    Parameter bean = beanParam("Prop1", "Prop2");

    List<DocParameter> params = gen.createParamList(bean);
    assertThat(params, hasSize(2));
  }

  @Test
  public void listParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator(docParameterGenerator_);
    Parameter list = listParam();

    List<DocParameter> params = gen.createParamList(list);
    assertThat(params, hasSize(1));
  }

  @Test
  public void setParamsGenerated() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator(docParameterGenerator_);
    Parameter set = setParam();

    List<DocParameter> params = gen.createParamList(set);
    assertThat(params, hasSize(1));
  }

  @Test
  public void itReadsJavadocCommentsForBeanProperties() throws IntrospectionException, ClassNotFoundException {
    QueryParamGenerator gen = new QueryParamGenerator(docParameterGenerator_);
    Parameter bean = beanParam("Prop1", "Prop2");
    gen.createParamList(bean);
    verify(docParameterGenerator_, times(2)).extractDocInfoFromMethod(any(MethodDoc.class));
  }

  private Parameter setParam() {
    return mockParam(mockType(false, Set.class.getName()));
  }

  private Parameter listParam() {
    return mockParam(mockType(false, List.class.getName()));
  }

  private Parameter beanParam(String... propNames) {
    Type type = mockType(false, "bean.Class");
    ClassDoc classDoc = mock(ClassDoc.class);
    when(type.asClassDoc()).thenReturn(classDoc);
    MethodDoc[] methodDocs = mockProps(propNames);
    when(classDoc.methods()).thenReturn(methodDocs);
    return mockParam(type);
  }

  private MethodDoc[] mockProps(String[] propNames) {
    MethodDoc[] methodDocs = new MethodDoc[propNames.length];
    for (int i = 0; i < propNames.length; i++) {
      methodDocs[i] = mock(MethodDoc.class);
      when(methodDocs[i].name()).thenReturn("get" + propNames[i]);
    }
    return methodDocs;
  }

  private Parameter wrapperParam() {
    return mockParam(mockType(false, Long.class.getName()));
  }

  private Parameter primitiveParam() {
    return mockParam(mockType(true, long.class.getName()));
  }

  private Type mockType(boolean primitive, String fqn) {
    Type type = mock(Type.class);
    when(type.isPrimitive()).thenReturn(primitive);
    when(type.qualifiedTypeName()).thenReturn(fqn);
    return type;
  }

  private Parameter mockParam(Type type) {
    Parameter parameter = mock(Parameter.class);
    when(parameter.type()).thenReturn(type);
    return parameter;
  }
}

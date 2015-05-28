package org.cloudifysource.restDoclet.exampleGenerators;

import java.util.ArrayList;
import java.util.List;

import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import com.sun.javadoc.*;

public class ExampleGeneratorTest {

  private ExampleGenerator exampleGenerator_;
  private RequestObjectCreator requestCreator_;
  private ResponseObjectCreator responseCreator_;
  private MethodDoc methodDoc_;

  @Before
  public void setup() {
    requestCreator_ = mock(RequestObjectCreator.class);
    responseCreator_ = mock(ResponseObjectCreator.class);
    exampleGenerator_ = new ExampleGenerator(requestCreator_, responseCreator_);
    methodDoc_ = mock(MethodDoc.class);
  }

  @Test
  public void exampleResponseCanHandleString() throws Exception {
    Type stringType = stringType();
    when(methodDoc_.returnType()).thenReturn(stringType);
    when(responseCreator_.createObject(any(ObjectType.class))).thenReturn("Obiwan");

    final DocJsonResponseExample example = exampleGenerator_.exampleResponse(methodDoc_);

    assertThat(example.generateJsonResponseBody().replaceAll("\\s+", ""), is("\"Obiwan\""));
    assertThat(example.getComments(), is(""));
  }

  @Test
  public void exampleResponseCanHandleListOfStrings() throws Exception {
    Type listType = listOfStringsType();
    when(methodDoc_.returnType()).thenReturn(listType);
    when(responseCreator_.createObject(any(ObjectType.class))).thenReturn(new String[]{"Jarjar", "Binks"});

    final DocJsonResponseExample example = exampleGenerator_.exampleResponse(methodDoc_);

    assertThat(example.generateJsonResponseBody().replaceAll("\\s+", ""), is("[\"Jarjar\",\"Binks\"]"));
    assertThat(example.getComments(), is(""));
  }

  @Test
  public void exampleRequestHandlesPathVariableParams() throws Exception {
    final Parameter stringParam = createParam("string0", stringType(), PathVariable.class);
    when(methodDoc_.parameters()).thenReturn(new Parameter[] {stringParam});
    when(methodDoc_.paramTags()).thenReturn(new ParamTag[0]);

    final DocJsonRequestExample example = exampleGenerator_.exampleRequest(methodDoc_);

    verify(requestCreator_, never()).createObject(any(ObjectType.class));
    assertThat(example, is(DocJsonRequestExample.EMPTY));

  }

  @Test
  public void exampleRequestHandlesRequestBodyParams() throws Exception {
    final Parameter bodyParam = createParam("body0", ExampleBody.type(), RequestBody.class);
    when(methodDoc_.parameters()).thenReturn(new Parameter[] {bodyParam});
    when(methodDoc_.paramTags()).thenReturn(new ParamTag[0]);
    when(requestCreator_.createObject(any(ObjectType.class))).thenReturn(new ExampleBody());

    final DocJsonRequestExample example = exampleGenerator_.exampleRequest(methodDoc_);

    final String stripped = example.generateJsonRequestBody().replaceAll("\\s+", "");
    assertThat("Expect id to be set", stripped.contains("\"id\":12345"));
    assertThat("Expect count to be set", stripped.contains("\"count\":null"));
    assertThat("Expect names to be set", stripped.contains("\"names\":[\"Example\",\"Names\"]"));
    assertThat(example.getComments(), is(""));
  }

  @Test
  public void exampleRequestHandlesRequestBodyParamsList() throws Exception {
    final Parameter bodyParam = createParam("body0", ExampleBodyList.type(), RequestBody.class);
    when(methodDoc_.parameters()).thenReturn(new Parameter[] {bodyParam});
    when(requestCreator_.createObject(any(ObjectType.class))).thenReturn(ImmutableList.of(new ExampleBody()));

    final DocJsonRequestExample example = exampleGenerator_.exampleRequest(methodDoc_);

    final String stripped = example.generateJsonRequestBody().replaceAll("\\s+", "");
    assertThat("Expect JSON array", stripped.charAt(0), is('['));
    assertThat("Expect JSON array", stripped.charAt(stripped.length() - 1), is(']'));
    assertThat("Expect id to be set", stripped.contains("\"id\":12345"));
    assertThat("Expect count to be set", stripped.contains("\"count\":null"));
    assertThat("Expect names to be set", stripped.contains("\"names\":[\"Example\",\"Names\"]"));
    assertThat(example.getComments(), is(""));
  }

  private Type stringType() {
    Type type = mock(Type.class);
    when(type.qualifiedTypeName()).thenReturn(String.class.getName());
    return type;
  }

  private Type listOfStringsType() {
    ParameterizedType type = mock(ParameterizedType.class);
    when(type.qualifiedTypeName()).thenReturn(List.class.getName());
    when(type.asParameterizedType()).thenReturn(type);
    final Type stringType = stringType();
    when(type.typeArguments()).thenReturn(new Type[] {stringType});
    return type;
  }

  private Parameter createParam(final String paramName, final Type paramType, final Class annotationClass) {
    final Parameter param = mock(Parameter.class);
    final AnnotationDesc annotationDesc = mock(AnnotationDesc.class);
    final AnnotationTypeDoc annotationTypeDoc = mock(AnnotationTypeDoc.class);
    final AnnotationDesc[] annotations = new AnnotationDesc[] {annotationDesc};

    when(param.name()).thenReturn(paramName);
    when(param.type()).thenReturn(paramType);
    when(param.annotations()).thenReturn(annotations);

    when(annotationDesc.annotationType()).thenReturn(annotationTypeDoc);
    when(annotationTypeDoc.typeName()).thenReturn(annotationClass.getSimpleName());
    when(annotationTypeDoc.simpleTypeName()).thenReturn(annotationClass.getSimpleName());

    return param;
  }

  private static class ExampleBody {

    private static Type type() {
      Type type = mock(Type.class);
      when(type.qualifiedTypeName()).thenReturn(ExampleBody.class.getName());
      return type;
    }

    public long getId() {
      return 12345L;
    }

    public Integer getCount() {
      return null;
    }

    public String[] getNames() {
      return new String[] {"Example", "Names"};
    }
  }

  private static class ExampleBodyList {

    private static Type type() {
      final Type[] typeArguments = new Type[] { ExampleBody.type() };

      final ParameterizedType parameterizedType = mock(ParameterizedType.class);
      when(parameterizedType.typeArguments()).thenReturn(typeArguments);

      final Type type = mock(Type.class);
      when(type.qualifiedTypeName()).thenReturn(ArrayList.class.getName());
      when(type.asParameterizedType()).thenReturn(parameterizedType);
      return type;
    }
  }
}

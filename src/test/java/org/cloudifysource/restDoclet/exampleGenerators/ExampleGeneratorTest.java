package org.cloudifysource.restDoclet.exampleGenerators;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;

/**
 * @author edward
 */
public class ExampleGeneratorTest {

  private ExampleGenerator exampleGenerator_;
  private ObjectCreator objectCreator_;
  private MethodDoc methodDoc_;

  @Before
  public void setup() {
    objectCreator_ = mock(ObjectCreator.class);
    exampleGenerator_ = new ExampleGenerator(objectCreator_);
    methodDoc_ = mock(MethodDoc.class);
  }

  @Test
  public void generateSimpleString() throws IllegalAccessException, ClassNotFoundException, IOException {
    Type stringType = stringType();
    when(methodDoc_.returnType()).thenReturn(stringType);
    exampleGenerator_.exampleResponse(methodDoc_);
    verify(objectCreator_).createObject(String.class);
  }

  @Test
  public void generateListOfStrings() throws IllegalAccessException, ClassNotFoundException, IOException {
    Type listType = listOfStringsType();
    when(methodDoc_.returnType()).thenReturn(listType);
    exampleGenerator_.exampleResponse(methodDoc_);
    verify(objectCreator_).createParameterizedObject(List.class, new Class[] { String.class });
  }

  private Type listOfStringsType() {
    Type stringType = stringType();
    ParameterizedType type = mock(ParameterizedType.class);
    when(type.qualifiedTypeName()).thenReturn(List.class.getName());
    when(type.asParameterizedType()).thenReturn(type);
    when(type.typeArguments()).thenReturn(new Type[] {stringType});
    return type;
  }

  private Type stringType() {
    Type type = mock(Type.class);
    when(type.qualifiedTypeName()).thenReturn(String.class.getName());
    return type;
  }
}

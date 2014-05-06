package org.cloudifysource.restDoclet.exampleGenerators;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Before;
import org.junit.Test;

/**
 * @author edward
 */
public class SerializationTest {
  private ObjectCreator objectCreator_;

  @Before
  public void setup() {
    objectCreator_ = new ObjectCreator();
  }

  @Test
  public void canSerializeADate() throws IllegalAccessException, IOException {
    new ObjectMapper()
            .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
            .writeValueAsString(
                    objectCreator_.createObject(Date.class));
  }

  @Test
  public void canSerializeAList() throws IOException {
    new ObjectMapper()
            .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
            .writeValueAsString(new LinkedList<String>());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void canSerializeATopLevelList() throws IllegalAccessException, IOException {
    List<String> listOfStrings = new LinkedList<String>();
    List<String> listObject = (List<String>) objectCreator_.createParameterizedObject(listOfStrings.getClass(),
            new Class[] { String.class });
    new ObjectMapper()
            .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
            .writeValueAsString(listObject);
  }
}

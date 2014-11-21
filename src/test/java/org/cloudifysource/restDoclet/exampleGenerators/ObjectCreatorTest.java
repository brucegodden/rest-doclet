package org.cloudifysource.restDoclet.exampleGenerators;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import org.cloudifysource.restDoclet.annotations.DocumentCommand;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.collect.Lists.newArrayList;


public class ObjectCreatorTest {

  private ObjectCreator objectCreator_;

  @Before
  public void setup() {
    objectCreator_ = new ObjectCreator();
  }

  @Test
  public void createsAnObject() throws Exception {
    assertThat(objectCreator_.createObject(Fish.class), instanceOf(Fish.class));
  }

  @Test
  public void setsAStringFieldOnAnObject() throws Exception {
    Fish fish = (Fish) objectCreator_.createObject(Fish.class);
    assertThat(fish.getName(), notNullValue());
    assertThat(fish.getName().length(), greaterThan(0));
  }

  @Test
  public void setsAPrimitiveFieldOnAnObject() throws Exception {
    Fish fish = (Fish) objectCreator_.createObject(Fish.class);
    assertThat(fish.getCount(), instanceOf(long.class));
  }

  @Test
  public void createsNestedClasses() throws Exception {
    FishBowl fishBowl = (FishBowl) objectCreator_.createObject(FishBowl.class);
    assertThat(fishBowl.getFish(), instanceOf(Fish.class));
    assertThat(fishBowl.getFish().getName(), notNullValue());
  }

  @Test
  public void createsLists() throws Exception, IOException {
    Aquarium aquarium = (Aquarium) objectCreator_.createObject(Aquarium.class);
    assertThat(aquarium.getFishes(), instanceOf(List.class));
    assertThat(aquarium.getFishes(), hasSize(greaterThan(0)));
    assertThat(aquarium.getFishes().get(0), instanceOf(Fish.class));
    assertThat(aquarium.getFishes().get(0).getName(), notNullValue());
  }

  @Test
  public void createsListsOfLists() throws IOException, Exception {
    SeaWorld seaWorld = (SeaWorld) objectCreator_.createObject(SeaWorld.class);
    new ObjectMapper()
            .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
            .writeValueAsString(seaWorld);
  }

  @Test
  public void createClassWithEnumField() throws Exception {
    ClassWithEnumField response = (ClassWithEnumField) objectCreator_.createObject(ClassWithEnumField.class);
    assertThat(response.getStatus(), is(ClassWithEnumField.Status.ACTIVATED));
  }

  @Test
  public void createsEmptyClasses() throws Exception {
    EmptyClass empty = (EmptyClass) objectCreator_.createObject(EmptyClass.class);
    assertThat(empty, notNullValue());
  }

  @Test
  public void canCreateAnAbstractClass() throws Exception {
    ClassWithAnAbstractClassInside classInside =
            (ClassWithAnAbstractClassInside) objectCreator_.createObject(ClassWithAnAbstractClassInside.class);

    assertThat(classInside.getTheAbstractClass(), notNullValue());
  }

  @Test
  public void canCallAbstractMethods() throws Exception {
    AbstractClass abstractClass = (AbstractClass) objectCreator_.createObject(AbstractClass.class);
    assertThat(abstractClass.getFoo(), notNullValue());
  }

  @Test
  public void canCreateDateField() throws Exception {
    ClassWithDate dateClass = (ClassWithDate) objectCreator_.createObject(ClassWithDate.class);
    assertThat(dateClass.getDate(), notNullValue());
  }

  @Test
  public void canCreateCalendarField() throws Exception {
    ClassWithCalendar calendarClass = (ClassWithCalendar) objectCreator_.createObject(ClassWithCalendar.class);
    assertThat(calendarClass.getCalendar(), notNullValue());
  }

  @Test
  public void canCreateAMap() throws Exception {
    ClassWithMap mapClass = (ClassWithMap) objectCreator_.createObject(ClassWithMap.class);
    assertThat(mapClass.getMap(), notNullValue());
    Iterator<Map.Entry<Long, String>> it = mapClass.getMap().entrySet().iterator();
    assertThat(it.hasNext(), is(true));
    Map.Entry<Long,String> entry = it.next();
    assertThat(entry.getKey(), notNullValue());
    assertThat(entry.getValue(), notNullValue());
    assertThat(it.hasNext(), is(false));
  }

  @Test
  public void canCreateASet() throws Exception {
    ClassWithSet setClass = (ClassWithSet) objectCreator_.createObject(ClassWithSet.class);
    assertThat(setClass.getSet(), notNullValue());
    Iterator<String> it = setClass.getSet().iterator();
    assertThat(it.hasNext(), is(true));
    String entry = it.next();
    assertThat(entry, notNullValue());
    assertThat(it.hasNext(), is(false));
  }

  @Test
  public void canCreateANestedInnerClass() throws Exception {
    ClassWithNestedInnerClass outerClass = (ClassWithNestedInnerClass) objectCreator_.createObject(ClassWithNestedInnerClass.class);
    assertThat(outerClass.getInner(), notNullValue());
    assertThat(outerClass.getInner().getCount(), isA(Integer.class));
  }

  @Test
  public void canCreateATopLevelList() throws Exception {
    List<String> stringList = newArrayList("a");
    Object listObject = objectCreator_.createObject(stringList.getClass());
    assertThat(listObject, instanceOf(List.class));
  }

  @Test(expected = NoSuchMethodException.class)
  public void doesNotCreateSetterFieldsIfNotAnnotated() throws Exception {
    Object objectWithGetter = objectCreator_.createObject(ClassWithSetterButNoAnnotation.class);
    objectWithGetter.getClass().getDeclaredMethod("getCount");
  }

  @Test
  public void canCreateSetterFields() throws Exception {
    Object objectWithGetters = objectCreator_.createObject(ClassWithSetters.class);
    assertThat(objectWithGetters, CoreMatchers.instanceOf(ClassWithSetters.class));
    assertHasMethod(objectWithGetters, "getFish", Fish.class);
    assertHasMethod(objectWithGetters, "getCount", int.class);
  }

  @Test
  public void canCreateConstructorFields() throws Exception {
    Object objectWithGetters = objectCreator_.createObject(ClassWithConstructorAnnotation.class);
    assertHasMethod(objectWithGetters, "getShrimp", Fish.class);
  }

  @Test
  public void canCreateArraySetterFields() throws Exception {
    Object objectWithGetters = objectCreator_.createObject(ClassWithListAndArraySetters.class);
    assertThat(objectWithGetters, CoreMatchers.instanceOf(ClassWithListAndArraySetters.class));
    assertHasMethod(objectWithGetters, "getBowls", FishBowl[].class);
    assertHasMethod(objectWithGetters, "getAquariums", Aquarium[].class);
  }

  @Test
  public void canCreateMapSetterFields() throws Exception {
    Object objectWithGetters = objectCreator_.createObject(ClassWithMapSetter.class);
    assertThat(objectWithGetters, CoreMatchers.instanceOf(ClassWithMapSetter.class));
    Map map = (Map) assertHasMethod(objectWithGetters, "getSeaWorlds", Map.class, true);
    assertThat(map.size(), is(1));
    assertThat(map.values().toArray()[0], CoreMatchers.instanceOf(SeaWorld.class));
  }

  private void assertHasMethod(final Object getterObject, final String methodName, final Class returnType) throws Exception {
    assertHasMethod(getterObject, methodName, returnType, false);
  }

  private Object assertHasMethod(final Object getterObject, final String methodName, final Class returnType, final boolean fetch) throws Exception {
    try {
      Method method = getterObject.getClass().getDeclaredMethod(methodName);
      assertThat("Wrong return type from " + methodName + "() method", method.getReturnType().equals(returnType));
      if (fetch) {
        return method.invoke(getterObject);
      }
    } catch (NoSuchMethodException e) {
      assertThat("No " + methodName + "() method defined", false);
    }
    return null;
  }

  static class EmptyClass {
    public static final EmptyClass NOTHING = new EmptyClass();
  }

  static class ClassWithAnAbstractClassInside {
    private AbstractClass abstractClass_;

    public AbstractClass getTheAbstractClass() {
      return abstractClass_;
    }
  }

  static abstract class AbstractClass {
    private String bar_;

    public abstract String getFoo();
  }

  static class Fish {
    private String name_;
    private Long count_;

    public String getName() {
      return name_;
    }

    public long getCount() {
      return count_;
    }
  }

  static class FishBowl {
    private Fish fish_;

    public Fish getFish() {
      return fish_;
    }
  }

  static class Aquarium {
    private List<Fish> fishes_;

    public List<Fish> getFishes() {
      return fishes_;
    }
  }

  static class SeaWorld {
    private List<Aquarium> aquariums_;

    public List<Aquarium> getAquariums()  {
      return aquariums_;
    }
  }

  public static class ClassWithEnumField {
    public enum Status {
      ACTIVATED,
      PENDING
    }

    private Status status_;

    public Status getStatus() {
      return status_;
    }
  }

  public static class ClassWithDate {
    private Date date_;

    public Date getDate() {
      return date_;
    }
  }

  public static class ClassWithCalendar {
    private Calendar calendar_;

    public Calendar getCalendar() {
      return calendar_;
    }
  }

  public static class ClassWithMap {
    Map<Long, String> hashMap_ ;

    public Map<Long, String> getMap() {
      return hashMap_;
    }
  }

  public static class ClassWithSet {
    Set<String> hashSet_;

    public Set<String> getSet() {
      return hashSet_;
    }
  }

  static class ClassWithNestedInnerClass {
    private Inner inner_;

    public Inner getInner() {
      return inner_;
    }

    class Inner {
      public Integer getCount() {
        return 0;
      }
    }
  }

  static class ClassWithSetterButNoAnnotation {
    public void setCount(final int count) {
    }
  }

  @DocumentCommand
  static class ClassWithSetters extends ClassWithSetterButNoAnnotation {
    public void setFish(final Fish fish) {
    }
  }

  @DocumentCommand
  static class ClassWithConstructorAnnotation {
    public ClassWithConstructorAnnotation(@JsonProperty("shrimp") Fish prawn) {
    }
  }

  @DocumentCommand
  static class ClassWithListAndArraySetters {
    public void setBowls(final List<FishBowl> bowls) {
    }

    public void setAquariums(final Aquarium[] aquariums) {
    }
  }

  @DocumentCommand
  static class ClassWithMapSetter {
    public void setSeaWorlds(final Map<String, SeaWorld> nationalAquaria) {
    }
  }
}

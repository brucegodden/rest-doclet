package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.collect.Lists.newArrayList;


public class RequestObjectCreatorTest {

  private RequestObjectCreator creator_;

  @Before
  public void setup() {
    creator_ = new RequestObjectCreator();
  }

  @Test
  public void createsAnObject() throws Exception {
    Object empty = creator_.createObject(EmptyClass.class);
    assertThat(empty, notNullValue());
  }

  @Test
  public void setsAStringFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(Fish.class);
    final String name = (String) callMethod(fish, "getName", String.class);
    assertThat(name, notNullValue());
    assertThat(name.length(), greaterThan(0));
  }

  @Test
  public void setsAPrimitiveFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(Fish.class);
    final Long count = (Long) callMethod(fish, "getCount", Long.class);
    assertThat(count, notNullValue());
  }

  @Test
  public void setsAWrapperFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(Fish.class);
    final Integer temp = (Integer) callMethod(fish, "getTemp", Integer.class);
    assertThat(temp, notNullValue());
  }

  @Test
  public void createsNestedClasses() throws Exception {
    final Object fishBowl = creator_.createObject(FishBowl.class);
    final Object fish = callMethod(fishBowl, "getFish", Object.class);
    assertThat(fish, notNullValue());
    final String name = (String) callMethod(fish, "getName", String.class);
    assertThat(name, notNullValue());
  }

  @Test
  public void createsLists() throws Exception {
    final Object aquarium = creator_.createObject(Aquarium.class);
    final List<Object> fishes = (List<Object>) callMethod(aquarium, "getFishes", List.class);
    assertThat(fishes, instanceOf(List.class));
    assertThat(fishes, hasSize(greaterThan(0)));
    assertThat(fishes.get(0), notNullValue());
    assertHasMethod(fishes.get(0), "getName");
  }

  @Test
  public void createsListsOfLists() throws Exception {
    final Object seaWorld = creator_.createObject(SeaWorld.class);
    final List<Object> aquariums = (List<Object>) callMethod(seaWorld, "getAquariums", List.class);
    assertThat(aquariums, instanceOf(List.class));
    assertThat(aquariums, hasSize(greaterThan(0)));
    assertThat(aquariums.get(0), notNullValue());
    assertHasMethod(aquariums.get(0), "getFishes");
  }

  @Test
  public void createClassWithEnumField() throws Exception {
    final Object response = creator_.createObject(ClassWithEnumField.class);
    final ClassWithEnumField.Status status = (ClassWithEnumField.Status) callMethod(response, "getStatus", ClassWithEnumField.Status.class);
    assertThat(status, notNullValue());
    assertThat(status, is(ClassWithEnumField.Status.ACTIVATED));
  }

  @Test
  public void canCreateAnInterfaceClass() throws Exception {
    final Object object = creator_.createObject(InterfaceClass.class);
    assertThat(object, notNullValue());
    final String bar = (String) callMethod(object, "getBar", String.class);
    assertThat(bar, notNullValue());
  }

  @Test
  public void canCreateAnAbstractClass() throws Exception {
    final Object object = creator_.createObject(AbstractClass.class);
    assertThat(object, notNullValue());
    final String foo = (String) callMethod(object, "getFoo", String.class);
    assertThat(foo, notNullValue());
  }

  @Test
  public void canCreateClassContainingAnAbstractClass() throws Exception {
    final Object outer = creator_.createObject(ClassWithAnAbstractClassInside.class);
    final Object inner = callMethod(outer, "getTheAbstractClass", Object.class);
    assertThat(inner, notNullValue());
    assertHasMethod(inner, "getFoo");
  }

  @Test
  public void canCreateDateField() throws Exception {
    final Object dateClass = creator_.createObject(ClassWithDate.class);
    final Date date = (Date) callMethod(dateClass, "getDate", Date.class);
    assertThat(date, notNullValue());
  }

  @Test
  public void canCreateCalendarField() throws Exception {
    final Object calendarClass = creator_.createObject(ClassWithCalendar.class);
    final Calendar calendar = (Calendar) callMethod(calendarClass, "getCalendar", Calendar.class);
    assertThat(calendar, notNullValue());
  }

  @Test
  public void canCreateAMap() throws Exception {
    final Object mapClass = creator_.createObject(ClassWithMap.class);
    final Map<Long, String> map = (Map<Long, String>) callMethod(mapClass, "getMap", Map.class);
    assertThat(map, notNullValue());
    Iterator<Map.Entry<Long, String>> it = map.entrySet().iterator();
    assertThat(it.hasNext(), is(true));
    Map.Entry<Long,String> entry = it.next();
    assertThat(entry.getKey(), notNullValue());
    assertThat(entry.getKey(), isA(Long.class));
    assertThat(entry.getValue(), notNullValue());
    assertThat(entry.getValue(), isA(String.class));
    assertThat(it.hasNext(), is(false)); // Only want one example
  }

  @Test
  public void canCreateASet() throws Exception {
    final Object setClass = creator_.createObject(ClassWithSet.class);
    final Set<String> set = (Set<String>) callMethod(setClass, "getSet", Set.class);
    assertThat(set, notNullValue());
    Iterator<String> it = set.iterator();
    assertThat(it.hasNext(), is(true));
    String entry = it.next();
    assertThat(entry, notNullValue());
    assertThat(entry, isA(String.class));
    assertThat(it.hasNext(), is(false)); // Only want one example
  }

  @Test
  public void canCreateANestedInnerClass() throws Exception {
    final Object outer = creator_.createObject(ClassWithNestedInnerClass.class);
    final Object inner = callMethod(outer, "getInner", Object.class);
    assertThat(inner, notNullValue());
    assertHasMethod(inner, "getCount");
  }

  @Test
  public void canCreateATopLevelList() throws Exception {
    final List<String> stringList = newArrayList("a");
    final Object listObject = creator_.createObject(stringList.getClass());
    assertThat(listObject, instanceOf(List.class));
  }

  @Test(expected = NoSuchMethodException.class)
  public void doesNotCreateSetterFieldsIfNotAnnotated() throws Exception {
    final Object objectWithGetter = creator_.createObject(ClassWithGetter.class);
    objectWithGetter.getClass().getDeclaredMethod("getCount");
  }

  @Test(expected = NoSuchMethodException.class)
  public void doesIgnoreGetters() throws Exception {
    final Object object = creator_.createObject(ClassWithGetter.class);
    object.getClass().getDeclaredMethod("getCount");
  }

  @Test
  public void canCreateConstructorFields() throws Exception {
    final Object object = creator_.createObject(ClassWithConstructorAnnotation.class);
    assertHasMethod(object, "getShrimp");
  }

  @Test
  public void canCreateArraySetterFields() throws Exception {
    final Object object = creator_.createObject(ClassWithListAndArraySetters.class);
    assertHasMethod(object, "getBowls");
    assertHasMethod(object, "getAquariums");
  }

  @Test
  public void canCreateMapSetterFields() throws Exception {
    final Object object = creator_.createObject(ClassWithMapSetter.class);
    final Map map = (Map) callMethod(object, "getSeaWorlds", Map.class);
    assertThat(map.size(), is(1));
    assertHasMethod(map.values().toArray()[0], "getAquariums");
  }

  private void assertHasMethod(final Object getterObject, final String methodName) throws Exception {
    assertHasMethod(getterObject, methodName, false);
  }

  private Object callMethod(final Object getterObject, final String methodName, final Class clazz) throws Exception {
    final Object object = assertHasMethod(getterObject, methodName, true);
    if (object != null) {
      assertThat("Wrong type object returned from " + methodName + "() method", clazz.isAssignableFrom(object.getClass()));
    }
    return object;
  }

  private Object assertHasMethod(final Object getterObject, final String methodName, final boolean fetch) throws Exception {
    try {
      assertThat(getterObject, notNullValue());
      Method method = getterObject.getClass().getDeclaredMethod(methodName);
      assertThat("Wrong return type from " + methodName + "() method", method.getReturnType().equals(Object.class));
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
    public void setTheAbstractClass(AbstractClass clazz) {
    }
  }

  interface InterfaceClass {
    void setBar(String bar);
  }

  static abstract class AbstractClass {
    public abstract void setFoo(String foo);
  }

  static class Fish {
    public void setName(String name) {
    }

    public void setCount(long count) {
    }

    public void setTemp(Integer temp) {
    }
  }

  static class FishBowl {
    public void setFish(Fish fish) {
    }
  }

  static class Aquarium {
    public void setFishes(List<Fish> fishes) {
    }
  }

  static class SeaWorld {
    public void setAquariums(List<Aquarium> aquariums)  {
    }
  }

  static class ClassWithEnumField {
    public enum Status {
      ACTIVATED,
      PENDING
    }

    public void setStatus(Status status) {
    }
  }

  static class ClassWithDate {
    public void setDate(Date date) {
    }
  }

  static class ClassWithCalendar {
    public void setCalendar(Calendar calendar) {
    }
  }

  static class ClassWithMap {
    public void setMap(Map<Long, String> map) {
    }
  }

  static class ClassWithSet {
    public void setSet(Set<String> set) {
    }
  }

  static class ClassWithNestedInnerClass {
    public void setInner(Inner inner) {
    }

    class Inner {
      public void setCount(Integer count) {
      }
    }
  }

  static class ClassWithGetter {
    public int getCount() {
      return 10;
    }
  }

  static class ClassWithConstructorAnnotation {
    public ClassWithConstructorAnnotation(@JsonProperty("shrimp") Fish prawn) {
    }
  }

  static class ClassWithListAndArraySetters {
    public void setBowls(final List<FishBowl> bowls) {
    }

    public void setAquariums(final Aquarium[] aquariums) {
    }
  }

  static class ClassWithMapSetter {
    public void setSeaWorlds(final Map<String, SeaWorld> nationalAquaria) {
    }
  }
}

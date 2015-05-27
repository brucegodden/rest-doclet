package org.cloudifysource.restDoclet.exampleGenerators;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static com.google.common.collect.Lists.newArrayList;


public class ResponseObjectCreatorTest {

  private ResponseObjectCreator creator_;

  @Before
  public void setup() {
    creator_ = new ResponseObjectCreator();
  }

  @Test
  public void createsAnObject() throws Exception {
    Object empty = creator_.createObject(new ObjectType(EmptyClass.class));
    assertThat(empty, notNullValue());
  }

  @Test
  public void setsAStringFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(new ObjectType(Fish.class));
    final String name = (String) callMethod(fish, "getName", String.class);
    assertThat(name, notNullValue());
    assertThat(name.length(), greaterThan(0));
  }

  @Test
  public void setsAGetPrimitiveFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(new ObjectType(Fish.class));
    final Long count = (Long) callMethod(fish, "getCount", Long.class);
    assertThat(count, notNullValue());
  }

  @Test
  public void setsAnIsPrimitiveFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(new ObjectType(Fish.class));
    final Boolean saltwater = (Boolean) callMethod(fish, "getSaltwater", Boolean.class);
    assertThat(saltwater, notNullValue());
  }

  @Test
  public void setsAGetWrapperFieldOnAnObject() throws Exception {
    final Object fish = creator_.createObject(new ObjectType(Fish.class));
    final Integer temp = (Integer) callMethod(fish, "getTemp", Integer.class);
    assertThat(temp, notNullValue());
  }

  @Test
  public void createsNestedClasses() throws Exception {
    final Object fishBowl = creator_.createObject(new ObjectType(FishBowl.class));
    final Object fish = callMethod(fishBowl, "getFish", Object.class);
    assertThat(fish, notNullValue());
    final String name = (String) callMethod(fish, "getName", String.class);
    assertThat(name, notNullValue());
  }

  @Test
  public void createsLists() throws Exception {
    final Object aquarium = creator_.createObject(new ObjectType(Aquarium.class));
    final List<Object> fishes = (List<Object>) callMethod(aquarium, "getFishes", List.class);
    assertThat(fishes, instanceOf(List.class));
    assertThat(fishes, hasSize(greaterThan(0)));
    assertThat(fishes.get(0), notNullValue());
    assertHasMethod(fishes.get(0), "getName");
  }

  @Test
  public void createsListsOfLists() throws Exception {
    final Object seaWorld = creator_.createObject(new ObjectType(SeaWorld.class));
    final List<Object> aquariums = (List<Object>) callMethod(seaWorld, "getAquariums", List.class);
    assertThat(aquariums, instanceOf(List.class));
    assertThat(aquariums, hasSize(greaterThan(0)));
    assertThat(aquariums.get(0), notNullValue());
    assertHasMethod(aquariums.get(0), "getFishes");
  }

  @Test
  public void createClassWithEnumField() throws Exception {
    final Object response = creator_.createObject(new ObjectType(ClassWithEnumField.class));
    final ClassWithEnumField.Status status = (ClassWithEnumField.Status) callMethod(response, "getStatus", ClassWithEnumField.Status.class);
    assertThat(status, notNullValue());
    assertThat(status, is(ClassWithEnumField.Status.ACTIVATED));
  }

  @Test
  public void canCreateAnInterfaceClass() throws Exception {
    final Object object = creator_.createObject(new ObjectType(InterfaceClass.class));
    assertThat(object, notNullValue());
    final String bar = (String) callMethod(object, "getBar", String.class);
    assertThat(bar, notNullValue());
  }

  @Test
  public void canCreateAnAbstractClass() throws Exception {
    final Object object = creator_.createObject(new ObjectType(AbstractClass.class));
    assertThat(object, notNullValue());
    final String foo = (String) callMethod(object, "getFoo", String.class);
    assertThat(foo, notNullValue());
  }

  @Test
  public void canCreateClassContainingAnAbstractClass() throws Exception {
    final Object outer = creator_.createObject(new ObjectType(ClassWithAnAbstractClassInside.class));
    final Object inner = callMethod(outer, "getTheAbstractClass", Object.class);
    assertThat(inner, notNullValue());
    assertHasMethod(inner, "getFoo");
  }

  @Test
  public void canCreateDateField() throws Exception {
    final Object dateClass = creator_.createObject(new ObjectType(ClassWithDate.class));
    final Date date = (Date) callMethod(dateClass, "getDate", Date.class);
    assertThat(date, notNullValue());
  }

  @Test
  public void canCreateCalendarField() throws Exception {
    final Object calendarClass = creator_.createObject(new ObjectType(ClassWithCalendar.class));
    final Calendar calendar = (Calendar) callMethod(calendarClass, "getCalendar", Calendar.class);
    assertThat(calendar, notNullValue());
  }

  @Test
  public void canCreateAMap() throws Exception {
    final Object mapClass = creator_.createObject(new ObjectType(ClassWithMap.class));
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
    final Object setClass = creator_.createObject(new ObjectType(ClassWithSet.class));
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
    final Object outer = creator_.createObject(new ObjectType(ClassWithNestedInnerClass.class));
    final Object inner = callMethod(outer, "getInner", Object.class);
    assertThat(inner, notNullValue());
    assertHasMethod(inner, "getCount");
  }

  @Test
  public void canCreateATopLevelList() throws Exception {
    final List<String> stringList = newArrayList();
    final Object listObject = creator_.createObject(new ObjectType(stringList.getClass()));
    assertThat(listObject, instanceOf(List.class));
  }

  @Test(expected = NoSuchMethodException.class)
  public void doesIgnoreSetters() throws Exception {
    Object object = creator_.createObject(new ObjectType(ClassWithSetter.class));
    object.getClass().getDeclaredMethod("getCount");
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
    private AbstractClass abstractClass_;

    public AbstractClass getTheAbstractClass() {
      return abstractClass_;
    }
  }

  interface InterfaceClass {
    String getBar();
  }

  static abstract class AbstractClass {
    private String bar_;

    public abstract String getFoo();
  }

  static class Fish {
    private String name_;
    private long count_;
    private boolean saltwater_;
    private Integer temp_;

    public String getName() {
      return name_;
    }

    public long getCount() {
      return count_;
    }

    public boolean isSaltwater() {
      return saltwater_;
    }

    public Integer getTemp() {
      return temp_;
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

  static class ClassWithEnumField {
    public enum Status {
      ACTIVATED,
      PENDING
    }

    private Status status_;

    public Status getStatus() {
      return status_;
    }
  }

  static class ClassWithDate {
    private Date date_;

    public Date getDate() {
      return date_;
    }
  }

  static class ClassWithCalendar {
    private Calendar calendar_;

    public Calendar getCalendar() {
      return calendar_;
    }
  }

  static class ClassWithMap {
    Map<Long, String> hashMap_ ;

    public Map<Long, String> getMap() {
      return hashMap_;
    }
  }

  static class ClassWithSet {
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

  static class ClassWithSetter {
    public void setCount(final int count) {
    }
  }
}

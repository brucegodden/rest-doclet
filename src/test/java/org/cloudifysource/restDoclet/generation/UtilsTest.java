package org.cloudifysource.restDoclet.generation;

import org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes;
import org.junit.Test;

import static org.cloudifysource.restDoclet.generation.Utils.filterOutControllerClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;

/**
 * @author bruce
 */
public class UtilsTest {
  @Test
  public void testFilterOutControllerClassWithNoControllerAnnotation() {
    final ClassDoc classDoc = mock(ClassDoc.class);
    final RestAnnotations annotations = mock(RestAnnotations.class);

    final boolean result = filterOutControllerClass(classDoc, annotations);

    assertThat(result, is(true));
  }

  @Test
  public void testFilterOutControllerClassWithControllerAnnotation() {
    final ClassDoc classDoc = mock(ClassDoc.class);
    final RestAnnotations annotations = mock(RestAnnotations.class);
    when(annotations.getAnnotation(DocAnnotationTypes.CONTROLLER)).thenReturn(mock(AnnotationDesc.class));

    final boolean result = filterOutControllerClass(classDoc, annotations);

    assertThat(result, is(false));
  }

  @Test
  public void testFilterOutControllerClassWithRestControllerAnnotation() {
    final ClassDoc classDoc = mock(ClassDoc.class);
    final RestAnnotations annotations = mock(RestAnnotations.class);
    when(annotations.getAnnotation(DocAnnotationTypes.REST_CONTROLLER)).thenReturn(mock(AnnotationDesc.class));

    final boolean result = filterOutControllerClass(classDoc, annotations);

    assertThat(result, is(false));
  }
}

package org.cloudifysource.restDoclet.generation;

import org.junit.Before;
import org.junit.Test;

import static org.cloudifysource.restDoclet.constants.RestDocConstants.REQUEST_MAPPING_ANNOTATION;
import static org.cloudifysource.restDoclet.constants.RestDocConstants.REQUEST_PARAMS_ANNOTATION;
import static org.cloudifysource.restDoclet.constants.RestDocConstants.REST_CONTROLLER_ANNOTATION;
import static org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes.REST_CONTROLLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import static com.google.common.collect.Lists.newArrayList;


/**
 * @author edward
 */
public class AnnotationReaderTest {

  private AnnotationReader reader_;

  @Before
  public void setup() {
    reader_ = new AnnotationReader();
  }

  @Test
  public void readsRequestMapping() {
    AnnotationDesc annotationDesc = createAnnotation(REQUEST_MAPPING_ANNOTATION);
    assertThat(reader_.read(newArrayList(annotationDesc), null).requestMappingAnnotation(), not(nullValue()));
  }

  @Test
  public void readsRequestParam() {
    AnnotationDesc annotationDesc = createAnnotation(REQUEST_PARAMS_ANNOTATION);
    assertThat(reader_.read(newArrayList(annotationDesc), null).requestParamAnnotation(), not(nullValue()));
  }

  @Test
  public void readsRestController() {
    AnnotationDesc annotationDesc = createAnnotation(REST_CONTROLLER_ANNOTATION);
    assertThat(reader_.read(newArrayList(annotationDesc), null).getAnnotation(REST_CONTROLLER), not(nullValue()));
  }

  private AnnotationDesc createAnnotation(String typeName) {
    AnnotationDesc annotationDesc = mock(AnnotationDesc.class);
    AnnotationTypeDoc annotationType = mock(AnnotationTypeDoc.class);
    when(annotationDesc.elementValues()).thenReturn(new AnnotationDesc.ElementValuePair[0]);
    when(annotationDesc.annotationType()).thenReturn(annotationType);
    when(annotationType.typeName()).thenReturn(typeName);
    return annotationDesc;
  }
}

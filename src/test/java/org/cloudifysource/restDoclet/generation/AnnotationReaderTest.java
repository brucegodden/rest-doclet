package org.cloudifysource.restDoclet.generation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cloudifysource.restDoclet.docElements.DocResponseStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.cloudifysource.restDoclet.constants.RestDocConstants.*;
import static org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

import static com.google.common.collect.Lists.newArrayList;


/**
 * @author edward
 */
public class AnnotationReaderTest {

  private static final int RESPONSE_CODE = 123;
  private static final String RESPONSE_DESCRIPTION = "Response description";

  private static final String PARAM_NAME = "paramName";
  private static final String PARAM_DESCRIPTION = "Param description";

  private AnnotationReader reader_;

  @Before
  public void setup() {
    reader_ = new AnnotationReader();
  }

  @Test
  public void readsRequestMapping() {
    assertThat(reader_.read(createAnnotations(REQUEST_MAPPING_ANNOTATION), null).requestMappingAnnotation().isPresent(), is(true));
    assertThat(reader_.read(createAnnotations(REST_CONTROLLER_ANNOTATION), null).requestMappingAnnotation().isPresent(), is(false));
  }

  @Test
  public void readsRequestParam() {
    assertThat(reader_.read(createAnnotations(REQUEST_PARAM_ANNOTATION), null).requestParamAnnotation(), is(true));
    assertThat(reader_.read(createAnnotations(REQUEST_HEADER_ANNOTATION), null).requestParamAnnotation(), is(false));
  }

  @Test
  public void readsRequestHeader() {
    assertThat(reader_.read(createAnnotations(REQUEST_HEADER_ANNOTATION), null).requestHeaderAnnotation(), is(true));
    assertThat(reader_.read(createAnnotations(REQUEST_PARAM_ANNOTATION), null).requestHeaderAnnotation(), is(false));
  }

  @Test
  public void readsRestController() {
    assertThat(reader_.read(createAnnotations(REST_CONTROLLER_ANNOTATION), null).getAnnotation(REST_CONTROLLER), not(nullValue()));
    assertThat(reader_.read(createAnnotations(REQUEST_MAPPING_ANNOTATION), null).getAnnotation(REST_CONTROLLER), nullValue());
  }

  @Test
  public void fetchesResponseDocumentation() {
    final Collection<Tag> tags = newArrayList(createResponseTag());

    final Collection<DocResponseStatus> result = reader_.read(createAnnotations(), tags).responseStatusCodes();

    assertThat(result, not(nullValue()));
    assertThat(result.size(), is(1));
    DocResponseStatus status = result.iterator().next();
    assertThat(status.getCode(), is(RESPONSE_CODE));
    assertThat(status.getDescription(), is(RESPONSE_DESCRIPTION));
  }

  @Test
  public void fetchesParamDocumentation() {
    final Collection<Tag> tags = newArrayList(createParamTag());

    final Map<String, String> result = reader_.read(createAnnotations(), tags).paramsDocumentation();

    assertThat(result, not(nullValue()));
    assertThat(result.keySet(), contains(PARAM_NAME));
    assertThat(result.get(PARAM_NAME), is(PARAM_DESCRIPTION));
  }

  private List<AnnotationDesc> createAnnotations(final String... annotations) {
    List<AnnotationDesc> descs = newArrayList();
    for (String annotation : annotations) {
      descs.add(createAnnotation(annotation));
    }
    return descs;
  }

  private AnnotationDesc createAnnotation(String typeName) {
    AnnotationDesc annotationDesc = mock(AnnotationDesc.class);
    AnnotationTypeDoc annotationType = mock(AnnotationTypeDoc.class);
    when(annotationDesc.elementValues()).thenReturn(new AnnotationDesc.ElementValuePair[0]);
    when(annotationDesc.annotationType()).thenReturn(annotationType);
    when(annotationType.typeName()).thenReturn(typeName);
    return annotationDesc;
  }

  private Tag createResponseTag() {
    final Tag tag = mock(Tag.class);
    when(tag.name()).thenReturn("@ResponseCode");
    when(tag.text()).thenReturn(RESPONSE_CODE + " " + RESPONSE_DESCRIPTION);
    return tag;
  }

  private Tag createParamTag() {
    final ParamTag tag = mock(ParamTag.class);
    when(tag.name()).thenReturn("@param");
    when(tag.parameterName()).thenReturn(PARAM_NAME);
    when(tag.parameterComment()).thenReturn(PARAM_DESCRIPTION);
    return tag;
  }
}

package org.cloudifysource.restDoclet.docElements;

import org.cloudifysource.restDoclet.constants.RestDocConstants;

import com.sun.javadoc.AnnotationDesc;

/**
 * @author edward
 */
public class DocJsonResponse extends DocAnnotation {
  public DocJsonResponse(final AnnotationDesc annotationDesc) {
    super(annotationDesc);
  }

  public String getResponse() {
    return getValue(RestDocConstants.RESPONSE_BODY_ANNOTATION).or("no response body").toString();
  }
}

package org.cloudifysource.restDoclet.generation;

import java.util.Collection;
import java.util.Map;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponse;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocResponseStatus;
import org.cloudifysource.restDoclet.docElements.DocRequestMappingAnnotation;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;

import com.google.common.base.Optional;
import com.sun.javadoc.AnnotationDesc;

/**
 * @author edward
 */
public interface RestAnnotations {
  public Optional<DocRequestMappingAnnotation> requestMappingAnnotation();
  public Optional<DocRequestParamAnnotation> requestParamAnnotation();
  public Optional<DocJsonResponseExample> jsonResponseExample();
  public Optional<DocJsonRequestExample> jsonRequestExample();
  public Optional<DocJsonResponse> responseBody();
  public Collection<DocResponseStatus> responseStatusCodes();
  public Map<String, String> paramsDocumentation();
  public AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type);
}

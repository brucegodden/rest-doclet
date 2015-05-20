package org.cloudifysource.restDoclet.generation;

import java.util.Collection;
import java.util.Map;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponse;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocResponseStatus;
import org.cloudifysource.restDoclet.docElements.DocRequestMappingAnnotation;

import com.google.common.base.Optional;
import com.sun.javadoc.AnnotationDesc;

/**
 * @author edward
 */
public interface RestAnnotations {
  Optional<DocRequestMappingAnnotation> requestMappingAnnotation();
  boolean requestParamAnnotation();
  boolean requestHeaderAnnotation();
  Optional<DocJsonResponseExample> jsonResponseExample();
  Optional<DocJsonRequestExample> jsonRequestExample();
  Optional<DocJsonResponse> responseBody();
  Collection<DocResponseStatus> responseStatusCodes();
  Map<String, String> paramsDocumentation();
  AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type);
}

package org.cloudifysource.restDoclet.generation;

import java.util.Collection;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponse;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocPossibleResponseStatuses;
import org.cloudifysource.restDoclet.docElements.DocResponseStatus;
import org.cloudifysource.restDoclet.docElements.DocRequestMappingAnnotation;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;

import com.sun.javadoc.AnnotationDesc;

/**
 * @author edward
 */
public interface RestAnnotations {
  public DocRequestMappingAnnotation requestMappingAnnotation();
  public DocRequestParamAnnotation requestParamAnnotation();
  public DocJsonResponseExample jsonResponseExample();
  public DocJsonRequestExample jsonRequestExample();
  public DocJsonResponse responseBody();
  public Collection<DocResponseStatus> responseStatusCodes();
  public AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type);
}

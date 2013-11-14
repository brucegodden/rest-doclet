package org.cloudifysource.restDoclet.generation;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocPossibleResponseStatusAnnotation;
import org.cloudifysource.restDoclet.docElements.DocPossibleResponseStatusesAnnotation;
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
  public DocPossibleResponseStatusAnnotation possibleResponseStatusAnnotation();
  public DocPossibleResponseStatusesAnnotation possibleResponseStatusesAnnotation();
  public AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type);
}

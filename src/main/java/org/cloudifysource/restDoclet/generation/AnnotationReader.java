package org.cloudifysource.restDoclet.generation;


import java.util.HashMap;
import java.util.Map;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocPossibleResponseStatusAnnotation;
import org.cloudifysource.restDoclet.docElements.DocPossibleResponseStatusesAnnotation;
import org.cloudifysource.restDoclet.docElements.DocRequestMappingAnnotation;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;

import com.sun.javadoc.AnnotationDesc;
import static org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes.*;

/**
 * @author edward
 */
public class AnnotationReader {
  public AnnotationReader() {}

  public RestAnnotations read(Iterable<AnnotationDesc> annotations) {
    //filter out default annotations
    final Map<RestDocConstants.DocAnnotationTypes, AnnotationDesc> annotationMap =
            new HashMap<RestDocConstants.DocAnnotationTypes, AnnotationDesc>();
    for (AnnotationDesc annotation : annotations) {
      annotationMap.put(RestDocConstants.DocAnnotationTypes.fromName(annotation.annotationType().typeName()), annotation);
    }

    return new RestAnnotations() {
      @Override
      public DocRequestMappingAnnotation requestMappingAnnotation() {
        AnnotationDesc annotationDesc = annotationMap.get(REQUEST_MAPPING);
        return  annotationDesc != null ? new DocRequestMappingAnnotation(annotationDesc) : null;
      }

      @Override
      public DocRequestParamAnnotation requestParamAnnotation() {
        AnnotationDesc annotationDesc = annotationMap.get(REQUEST_PARAM);
        return annotationDesc != null ? new DocRequestParamAnnotation(annotationDesc) : null;
      }

      @Override
      public DocJsonResponseExample jsonResponseExample() {
        AnnotationDesc annotationDesc = annotationMap.get(JSON_RESPONSE_EXAMPLE);
        return annotationDesc != null ? new DocJsonResponseExample(annotationDesc) : null;
      }

      @Override
      public DocJsonRequestExample jsonRequestExample() {
        AnnotationDesc annotationDesc = annotationMap.get(JSON_REQUEST_EXAMPLE);
        return annotationDesc != null ? new DocJsonRequestExample(annotationDesc) : null;
      }

      @Override
      public DocPossibleResponseStatusAnnotation possibleResponseStatusAnnotation() {
        AnnotationDesc annotationDesc = annotationMap.get(POSSIBLE_RESPONSE_STATUS);
        return annotationDesc != null ? new DocPossibleResponseStatusAnnotation(annotationDesc) : null;
      }

      @Override
      public DocPossibleResponseStatusesAnnotation possibleResponseStatusesAnnotation() {
        AnnotationDesc annotationDesc = annotationMap.get(POSSIBLE_RESPONSE_STATUSES);
        return annotationDesc != null ? new DocPossibleResponseStatusesAnnotation(annotationDesc) : null;
      }

      @Override
      public AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type) {
        return annotationMap.get(type);
      }
    };
  }
}

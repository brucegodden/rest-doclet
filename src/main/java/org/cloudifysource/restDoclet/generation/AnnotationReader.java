package org.cloudifysource.restDoclet.generation;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocResponseStatus;
import org.cloudifysource.restDoclet.docElements.DocRequestMappingAnnotation;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Tag;

import static org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes.*;

/**
 * @author edward
 */
public class AnnotationReader {
  private Predicate<Tag> isResponseTag = new Predicate<Tag>() {
    @Override
    public boolean apply(@Nullable final Tag input) {
      return input != null && input.name().equals("@ResponseCode");
    }
  };

  private Function<Tag, DocResponseStatus> intoResponseDoc = new Function<Tag, DocResponseStatus>() {
    @Override
    public DocResponseStatus apply(final Tag input) {
      return new DocResponseStatus(input);
    }
  };

  public AnnotationReader() {}

  public RestAnnotations read(Iterable<AnnotationDesc> annotations, final Collection<Tag> tags) {
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
      public Collection<DocResponseStatus> responseStatusCodes() {
        Collection<Tag> responseTags = Collections2.filter(tags, isResponseTag);
        return Collections2.transform(responseTags, intoResponseDoc);
      }

      @Override
      public AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type) {
        return annotationMap.get(type);
      }
    };
  }
}

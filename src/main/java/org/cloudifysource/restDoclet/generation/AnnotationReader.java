package org.cloudifysource.restDoclet.generation;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.DocAnnotation;
import org.cloudifysource.restDoclet.docElements.DocJsonRequestExample;
import org.cloudifysource.restDoclet.docElements.DocJsonResponse;
import org.cloudifysource.restDoclet.docElements.DocJsonResponseExample;
import org.cloudifysource.restDoclet.docElements.DocResponseStatus;
import org.cloudifysource.restDoclet.docElements.DocRequestMappingAnnotation;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

import static org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes.*;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Maps.newHashMap;

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

  private Predicate<Tag> isParamTag = new Predicate<Tag>() {
    @Override
    public boolean apply(@Nullable final Tag input) {
      return input != null && input.name().equals("@param");
    }
  };

  public AnnotationReader() {}

  public RestAnnotations read(Iterable<AnnotationDesc> annotations, final Collection<Tag> tags) {
    //filter out default annotations
    final Map<RestDocConstants.DocAnnotationTypes, AnnotationDesc> annotationMap = new HashMap<RestDocConstants.DocAnnotationTypes, AnnotationDesc>();
    for (AnnotationDesc annotation : annotations) {
      annotationMap.put(RestDocConstants.DocAnnotationTypes.fromName(annotation.annotationType().typeName()),
              annotation);
    }

    return new RestAnnotations() {
      @Override
      public Optional<DocRequestMappingAnnotation> requestMappingAnnotation() {
        AnnotationDesc annotationDesc = annotationMap.get(REQUEST_MAPPING);
        return  annotationDesc != null
                ? Optional.of(new DocRequestMappingAnnotation(annotationDesc))
                : Optional.<DocRequestMappingAnnotation>absent();
      }

      @Override
      public boolean requestParamAnnotation() {
        return annotationMap.get(REQUEST_PARAM) != null;
      }

      @Override
      public boolean requestHeaderAnnotation() {
        return annotationMap.get(REQUEST_HEADER) != null;
      }

      @Override
      public Optional<DocJsonResponseExample> jsonResponseExample() {
        AnnotationDesc annotationDesc = annotationMap.get(JSON_RESPONSE_EXAMPLE);
        if (annotationDesc != null) {
          Optional<String> value = new DocAnnotation(annotationDesc).getStringValue(RestDocConstants.JSON_RESPONSE_EXAMPLE_RESPONSE);
          if (value.isPresent()) {
            return Optional.of(new DocJsonResponseExample(value.get(), ""));
          }
        }
        return Optional.absent();
      }

      @Override
      public Optional<DocJsonRequestExample> jsonRequestExample() {
        AnnotationDesc annotationDesc = annotationMap.get(JSON_REQUEST_EXAMPLE);
        if (annotationDesc != null) {
          Optional<String> value = new DocAnnotation(annotationDesc).getStringValue(RestDocConstants.JSON_REQUEST_EXAMPLE_REQUEST_PARAMS);
          if (value.isPresent()) {
            return Optional.of(new DocJsonRequestExample(value.get(), ""));
          }
        }
        return Optional.absent();
      }

      @Override
      public Optional<DocJsonResponse> responseBody() {
        AnnotationDesc annotationDesc = annotationMap.get(RESPONSE_BODY);
        return annotationDesc != null
                ? Optional.of(new DocJsonResponse(annotationDesc))
                : Optional.<DocJsonResponse>absent();
      }

      @Override
      public Collection<DocResponseStatus> responseStatusCodes() {
        Collection<Tag> responseTags = filter(tags, isResponseTag);
        return transform(responseTags, intoResponseDoc);
      }

      @Override
      public Map<String, String> paramsDocumentation() {
        Map<String, String> params = newHashMap();
        for (Tag tag : filter(tags, isParamTag)) {
          ParamTag paramTag = (ParamTag) tag;
          params.put(paramTag.parameterName(), paramTag.parameterComment());
        }
        return params;
      }

      @Override
      public AnnotationDesc getAnnotation(RestDocConstants.DocAnnotationTypes type) {
        return annotationMap.get(type);
      }
    };
  }
}

/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.cloudifysource.restDoclet.docElements;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.cloudifysource.restDoclet.constants.RestDocConstants;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sun.javadoc.AnnotationDesc;


/**
 *
 * @author yael
 *
 */
public class DocRequestMappingAnnotation extends DocAnnotation {
  private static final List<String> EMPTY_LIST = Collections.emptyList();
  private static final Function<String, String> SIMPLE_NAME_FUN = new Function<String, String>() {
    @Nullable
    @Override
    public String apply(@Nullable final String input) {
      if (input == null) {
        return null;
      }
      return input.substring(input.lastIndexOf('.') + 1, input.length());
    }
  };

	public DocRequestMappingAnnotation(final AnnotationDesc annotationDesc) {
		super(annotationDesc);
  }

	public List<String> getValue() {
    return getStringListValue(RestDocConstants.REQUEST_MAPPING_VALUE).or(EMPTY_LIST);
  }

	public List<String> getMethod() {
    return Lists.transform(getStringListValue(RestDocConstants.REQUEST_MAPPING_METHOD).or(EMPTY_LIST), SIMPLE_NAME_FUN);
	}

	public List<String> headers() {
    return getStringListValue(RestDocConstants.REQUEST_MAPPING_HEADERS).or(EMPTY_LIST);
	}

  public List<String> getParams() {
		return getStringListValue(RestDocConstants.REQUEST_MAPPING_PRODUCES).or(EMPTY_LIST);
	}

	public List<String> getProduces() {
		return getStringListValue(RestDocConstants.REQUEST_MAPPING_PRODUCES).or(EMPTY_LIST);
	}

	public List<String> getConsumes() {
		return getStringListValue(RestDocConstants.REQUEST_MAPPING_CONSUMED).or(EMPTY_LIST);
	}

}

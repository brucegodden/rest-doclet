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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.tools.javadoc.AnnotationDescImpl;
import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author yael
 */
public class DocAnnotation {
	private final String name_;
  private final Function<AnnotationDesc.ElementValuePair, String> annotationDescKeyFn_ = new Function<AnnotationDesc.ElementValuePair, String>() {
    @Override
    public String apply(final AnnotationDesc.ElementValuePair input) {
      String name = input.element().toString();
      int beginIndex = name.lastIndexOf('.') + 1;
      int endIndex = name.lastIndexOf("()");
      if (endIndex == -1) {
        endIndex = name.length();
      }
      return name.substring(beginIndex, endIndex);
    }
  };

  private final ImmutableMap<String, AnnotationDesc.ElementValuePair> attributeMap_;

  public DocAnnotation(AnnotationDesc annotationDesc) {
    name_ = annotationDesc.annotationType() == null
            ? annotationDesc.toString()
            : annotationDesc.annotationType().typeName();

    attributeMap_ = Maps.uniqueIndex(Arrays.asList(annotationDesc.elementValues()), annotationDescKeyFn_);
  }

	public String getName() {
		return name_;
	}

  public Optional<Object> getValue(String attributeName) {
    AnnotationDesc.ElementValuePair value = attributeMap_.get(attributeName);
    return value != null ? Optional.of(constructAttrValue(value.value().value())) : Optional.absent();
  }

  public Optional<String> getStringValue(String attributeName) {
    return getStringListValue(attributeName).transform(new Function<List<String>, String>() {
      @Nullable
      @Override
      public String apply(@Nullable final List<String> input) {
        return Joiner.on(",").join(input);
      }
    });
  }

  public Optional<List<String>> getStringListValue(String attributeName) {
    return getValue(attributeName).transform(new Function<Object, List<String>>() {
      @Override
      public List<String> apply(final Object attribute) {
        if (attribute.getClass().isArray()) {
          return Lists.transform(Arrays.asList((Object[]) attribute), stringValueOf_);
        }
        else {
          return newArrayList(String.valueOf(attribute));
        }
      }
    });
  }

  private final Function<Object, String> stringValueOf_ = new Function<Object, String>() {
    @Nullable
    @Override
    public String apply(@Nullable final Object input) {
      return String.valueOf(input);
    }
  };

	/**
	 *
	 * @param value .
	 * @return Construct the value.
	 */
	public static Object constructAttrValue(final Object value) {
		if (value.getClass().isArray()) {
			AnnotationValue[] values = (AnnotationValue[]) value;
			Object firstValue = values[0].value();
			Object constractedValues = null;
			if (firstValue instanceof AnnotationDescImpl) {
				Class<?> annotationClass =
						DocAnnotationTypes.getAnnotationClass(
								((AnnotationDescImpl) firstValue).annotationType().typeName());
				constractedValues = Array.newInstance(annotationClass, values.length);
			} else {
				constractedValues = Array.newInstance(firstValue.getClass(),
						values.length);
			}
			for (int i = 0; i < values.length; i++) {
				Object currentValue = constructAttrValue(values[i].value());
				Array.set(constractedValues, i, currentValue);
			}
			return constractedValues;
		}

		if (value instanceof AnnotationDesc) {
			return null;
		}
		return value;
	}

	@Override
	public String toString() {
		String str = "@" + name_ + " ";
		if (attributeMap_ != null && attributeMap_.size() > 0) {
			str += attributeMap_;
		} else {
			str += "{No attributes}";
		}
		return str;
	}

}

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

/**
 *
 * @author yael
 *
 */
public class DocParameter {
	private final String name_;
  private final String type_;
	private final DocRequestParamAnnotation requestParamAnnotation_;

  private String description_;

  public DocParameter(final String name, final String type, final DocRequestParamAnnotation annotation) {
		name_ = name;
    type_ = type;
    requestParamAnnotation_ = annotation;
  }

	public String getName() {
		return name_;
	}

  public String getType() {
    return type_;
  }

	public String getDescription() {
		return description_;
	}

  public String getRequestParamAnnotation() {
    return requestParamAnnotation_.getValue();
  }

	/**
	 *
	 * @param description .
	 */
	public void setDescription(final String description) {
		String trimedDescription = description.trim();
		if (description.startsWith("-")) {
			trimedDescription = description.substring(1).trim();
		}
		description_ = trimedDescription;
	}

	/**
	 *
	 * @return The value of the required attribute of the RequestParam annotation.
	 */
	public boolean isRequired() {
    return requestParamAnnotation_.getDefaultValue() == null && requestParamAnnotation_.isRequired();
  }

	public String getLocation() {
		return requestParamAnnotation_.getName();
	}

	/**
	 *
	 * @return The value of the defaultValue attribute of the RequestParam annotation.
	 */
	public String getDefaultValue() {
		return requestParamAnnotation_.getDefaultValue();
	}
}

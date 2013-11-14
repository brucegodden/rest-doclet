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

import java.util.List;

import org.cloudifysource.restDoclet.constants.RestDocConstants.DocAnnotationTypes;

import com.sun.javadoc.Type;

/**
 *
 * @author yael
 *
 */
public class DocParameter {
	private final Type type;
	private final String name;
	private String description;
	private String location;

	private DocRequestParamAnnotation requestParamAnnotation_;
	private DocAnnotation requestBodyAnnotation;

	public DocParameter(final String name, final Type type) {
		this.name = name;
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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
		this.description = trimedDescription;
	}

	/**
	 *
	 * @return The value of the required attribute of the RequestParam annotation.
	 */
	public boolean isRequired() {
		if (requestParamAnnotation_ != null) {
			return requestParamAnnotation_.isRequired();
		}
		return true;
	}

	public String getLocation() {
		return location;
	}

  public void setLocation(String location) {
    this.location = location;
  }

	/**
	 *
	 * @return The value of the defaultValue attribute of the RequestParam annotation.
	 */
	public String getDefaultValue() {
		if (requestParamAnnotation_ != null) {
			return requestParamAnnotation_.getDefaultValue();
		}
		return null;
	}

	public DocRequestParamAnnotation getRequestParamAnnotation() {
		return requestParamAnnotation_;
	}

	public DocAnnotation getRequestBodyAnnotation() {
		return requestBodyAnnotation;
	}

  public void setRequestParamAnnotation(final DocRequestParamAnnotation requestParamAnnotation) {
    requestParamAnnotation_ = requestParamAnnotation;
  }
}

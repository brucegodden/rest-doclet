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

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author yael
 *
 */
public class DocParameter {

	private String actualName_;
	private String logicalName_;
	private String type_;
	private String location_;
	private String defaultValue_ = "";
	private boolean isRequired_ = false;
  private String description_ = "";

  public DocParameter(final String actualName, final String type, final String location, final DocRequestParamAnnotation annotation) {
		this(actualName, annotation.getValue(), type, location);
		defaultValue_ = String.valueOf(annotation.getDefaultValue());
		isRequired_ = defaultValue_.isEmpty() && annotation.isRequired();
	}

	public DocParameter(final String actualName, final String logicalName, final String type, final String location) {
		actualName_ = actualName;
		logicalName_ = StringUtils.isBlank(logicalName) ? actualName : logicalName;
		type_ = type.replace("java.lang.", "").replace("java.util.", "");
		location_ = location;
	}

	public String getActualName() {
		return actualName_;
	}

	public String getLogicalName() {
		return logicalName_;
	}

  public String getType() {
    return type_;
  }

	public String getDescription() {
		return description_;
	}

	/**
	 *
	 * @param description .
	 */
	public void setDescription(final String description) {
		String trimmed = description.trim();
		if (description.startsWith("-")) {
			trimmed = description.substring(1);
		}
    if (trimmed.endsWith(".")) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }
		description_ = trimmed.trim();
	}

	/**
	 *
	 * @return The value of the required attribute of the RequestParam annotation.
	 */
	public boolean isRequired() {
    return isRequired_;
  }

	public String getLocation() {
		return location_;
	}

	/**
	 *
	 * @return The value of the defaultValue attribute of the RequestParam annotation.
	 */
	public String getDefaultValue() {
		return defaultValue_;
	}
}

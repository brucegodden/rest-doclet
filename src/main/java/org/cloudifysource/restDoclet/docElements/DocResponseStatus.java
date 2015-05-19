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

import org.springframework.http.HttpStatus;

import com.sun.javadoc.Tag;

public class DocResponseStatus {

  private int code = 0;
  private String description;


	public DocResponseStatus(final Tag responseTag) {
    try {
      code = Integer.parseInt(firstWord(responseTag.text()));
      description = textAfterFirstWord(responseTag.text());
    } catch(NumberFormatException nfe) {
      description = responseTag.text();
    }
	}

	public int getCode() {
    return code;
	}

	public String getCodeName() {
		return HttpStatus.valueOf(code).name();
	}

  public String getDescription() {
    return description;
  }

  private String firstWord(String text) {
    return text.contains(" ") ? text.substring(0, text.indexOf(" ")) : text;
  }

  private String textAfterFirstWord(String text) {
    return text.contains(" ") ? text.substring(text.indexOf(" ")).trim() : text;
  }
}

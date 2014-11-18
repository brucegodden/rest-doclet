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

import java.io.IOException;

/**
 * @author yael
 */
public class DocJsonResponseExample{
  public static final DocJsonResponseExample EMPTY = new DocJsonResponseExample("", "") {
    public String generateJsonResponseBody() throws IOException {
      return "response has no body";
    }
  };

  private String example_;
  private String comments_;

	public DocJsonResponseExample(String example, String comments) {
		example_ = example;
    comments_ = comments;
	}

	public String getComments() {
    return comments_;
	}

	/**
	 *
	 * @return The response body in Json format.
	 * @throws IOException .
	 */
	public String generateJsonResponseBody() throws IOException {
		return example_;
	}
}

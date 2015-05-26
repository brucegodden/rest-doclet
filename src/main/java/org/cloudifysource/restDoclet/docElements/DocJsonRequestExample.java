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
 * @author yael
 */
public class DocJsonRequestExample  {
  private String jsonString_;
  private String comments_;

  public static final DocJsonRequestExample EMPTY = new DocJsonRequestExample("Request has no body", "");

  public DocJsonRequestExample(String example, String comments) {
    jsonString_ = example;
    comments_ = comments;
  }
  public String getComments() {
    return comments_;
  }

  /**
   * @return The request body in Json format.
   */
  public String generateJsonRequestBody() {
    return jsonString_;
  }
}

/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
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
package org.cloudifysource.restDoclet.exampleGenerators;

/**
 * Creates examples of primitive types.
 * @author yael
 * @since 0.5.0
 */
public final class PrimitiveExampleValues {
  
  private PrimitiveExampleValues() {
    
  }
  
  // These gets initialized to their default values
  private static final Boolean DEFAULT_BOOLEAN = true;
  private static final Byte DEFAULT_BYTE = 1;
  private static final Short DEFAULT_SHORT = 2;
  private static final Integer DEFAULT_INT = 3;
  private static final Long DEFAULT_LONG = 4L;
  private static final Float DEFAULT_FLOAT = 5.0F;
  private static final Double DEFAULT_DOUBLE = 6.0D;
  private static final Character DEFAULT_CHAR = 'a';
  
  /**
   * Returns an example value of a primitive type.
   * @param clazz the primitive type.
   * @return an example value of the given primitive type.
   */
  public static Object getValue(final Class<?> clazz) {
    if (clazz.equals(boolean.class)) {
      return DEFAULT_BOOLEAN;
    } else if (clazz.equals(byte.class)) {
      return DEFAULT_BYTE;
    } else if (clazz.equals(short.class)) {
      return DEFAULT_SHORT;
    } else if (clazz.equals(int.class)) {
      return DEFAULT_INT;
    } else if (clazz.equals(long.class)) {
      return DEFAULT_LONG;
    } else if (clazz.equals(float.class)) {
      return DEFAULT_FLOAT;
    } else if (clazz.equals(double.class)) {
      return DEFAULT_DOUBLE;
    } else if (clazz.equals(char.class)) {
      return DEFAULT_CHAR;
    } else {
      throw new IllegalArgumentException(
          "Class type " + clazz + " not supported");
    }
  }
}

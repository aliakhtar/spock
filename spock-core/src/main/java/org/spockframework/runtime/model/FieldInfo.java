/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.runtime.model;

import java.lang.reflect.Field;

import spock.lang.Shared;

/**
 * Runtime information about a field in a Spock specification.
 * 
 * @author Peter Niederwieser
 */
public class FieldInfo extends NodeInfo<SpeckInfo, Field> {
  private int ordinal;
  private int line;

  public int getOrdinal() {
    return ordinal;
  }

  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }

  public boolean isShared() {
    return getReflection().isAnnotationPresent(Shared.class);
  }

  public int getLine() {
    return line;
  }
  
  public void setLine(int line) {
    this.line = line;
  }
}

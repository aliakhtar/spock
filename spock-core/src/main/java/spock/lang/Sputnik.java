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

package spock.lang;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;

import org.spockframework.runtime.*;
import org.spockframework.runtime.model.SpeckInfo;

/**
 * A JUnit runner for Spock specifications. To run a specification with JUnit,
 * annotate it with <tt>@org.junit.RunWith(Sputnik)</tt>. In case you wondered,
 * Sputnik is a combination of the words "Spock" and "JUnit".
 *
 * @author Peter Niederwieser
 */
public class Sputnik extends Runner implements Filterable, Sortable {
  private final SpeckInfo speck;

  // TODO: we probably shouldn't just throw these exceptions as-is
  public Sputnik(Class<?> clazz) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
    speck = new SpeckInfoBuilder(clazz).build();
    new JUnitMetadataGenerator(speck).generate();
  }

  public Description getDescription() {
    return (Description)speck.getMetadata();
  }

  public void run(RunNotifier notifier) {
    new ParameterizedSpeckRunner(speck, new JUnitSupervisor(notifier)).run();
  }

  public void filter(Filter filter) throws NoTestsRemainException {
    speck.filterFeatures(new JUnitFilterAdapter(filter));
    if (speck.getFeatures().size() == 0)
      throw new NoTestsRemainException();
  }

  public void sort(Sorter sorter) {
    speck.sortFeatures(new JUnitSorterAdapter(sorter));
  }
}

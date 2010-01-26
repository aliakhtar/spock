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

package spock.util

import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.junit.Test
import org.junit.runner.RunWith
import org.spockframework.runtime.SpecUtil
import org.spockframework.util.NotThreadSafe
import spock.lang.Specification

/**
 * Utility class for programmatically compiling specs.
 * Mainly intended for testing purposes.
 * 
 * @author Peter Niederwieser
 */
@NotThreadSafe
class EmbeddedSpecCompiler {
  final GroovyClassLoader loader = new GroovyClassLoader()

  boolean unwrapCompileException = true

  String imports = ""

  void addImport(Package pkg) {
    imports += "import ${pkg.name}.*;"
  }
  
  /**
   * Compiles the given source code, and returns all Spock specifications
   * contained therein (but not other classes).
   */
  List compile(String source) {
    doCompile(imports + source)
  }

  List compileWithImports(String source) {
    addImport(Specification.package )
    // one-liner keeps line numbers intact
    doCompile "package apackage; $imports ${source.trim()}"
  }

  Class compileSpecBody(String source) {
    // one-liner keeps line numbers intact; newline safeguards against source ending in a line comment
    compileWithImports("class ASpec extends Specification { ${source.trim() + '\n'} }")[0]
  }

  Class compileFeatureBody(String source) {
    // one-liner keeps line numbers intact; newline safeguards against source ending in a line comment
    compileSpecBody "def 'a feature'() { ${source.trim() + '\n'} }"
  }

  private List doCompile(String source) {
    loader.clearCache()

    try {
    loader.parseClass(source.trim())
    } catch (MultipleCompilationErrorsException e) {
      def errors = e.errorCollector.errors
      if (unwrapCompileException && errors.size() == 1 && errors[0].hasProperty("cause"))
        throw errors[0].cause
      throw e
    }

    loader.loadedClasses.findAll {
      SpecUtil.isSpec(it) || isJUnitTest(it) // need JUnit tests sometimes
    } as List
  }
  
  private boolean isJUnitTest(Class clazz) {
    clazz.isAnnotationPresent(RunWith) || clazz.methods.any { it.getAnnotation(Test) }
  }
}
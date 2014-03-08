/*
 * Copyright 2001-2014 Artima, Inc.
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
package org.scalatestplus.play

import play.api.test._
import org.scalatest._

/**
 * Trait that provides new <code>FakeApplication</code> instance for each test.
 * 
 * It overrides ScalaTest's <code>withFixture</code> method to create new <code>FakeApplication</code> instance,
 * make it available from method <code>app</code>, and execute the test surrounded by a call to <code>Helpers.running(app)</code>.
 * In the tests you can access the <code>FakeApplication</code> using the <code>app</code> parameterless method.
 */
trait OneAppPerTest extends SuiteMixin { this: Suite => 

  /**
   * Method to create new instance of <code>FakeApplication</code>
   */
  def newApp: FakeApplication = new FakeApplication()
  private var appPerTest: FakeApplication = _

  /**
   * Implicit method that returns the <code>FakeApplication</code> instance for the current test.
   */
  implicit final def app: FakeApplication = synchronized { appPerTest }

  /**
   * Overriden to create new <code>FakeApplication</code> instance and run it before executing each test.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the <code>Outcome</code> of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized { appPerTest = newApp }
    Helpers.running(app) {
      super.withFixture(test)
    } 
  } 
}   


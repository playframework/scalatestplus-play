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
 * Trait that provides a new `FakeApplication` instance for each test.
 * 
 * This `SuiteMixin` trait's overridden `withFixture` method creates a new `FakeApplication` 
 * before each test and ensures it is cleaned up after the test has completed. You can
 * access the `FakeApplication` from your tests as method `app` (which is marked implicit).
 */
trait OneAppPerTest extends SuiteMixin { this: Suite => 

  /**
   * Creates new instance of `FakeApplication` with parameters set to their defaults. Override this method if you
   * need a `FakeApplication` created with non-default parameter values.
   */
  def newApp: FakeApplication = new FakeApplication()
  private var appPerTest: FakeApplication = _

  /**
   * Implicit method that returns the `FakeApplication` instance for the current test.
   */
  implicit final def app: FakeApplication = synchronized { appPerTest }

  /**
   * Creates a new `FakeApplication` instance before executing each test, and 
   * ensure they are cleaned up after the test completes. You can access the `FakeApplication` from
   * your tests via `app`.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized { appPerTest = newApp }
    Helpers.running(app) {
      super.withFixture(test)
    } 
  } 
}   


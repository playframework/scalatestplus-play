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

import play.api.Application
import play.api.test._
import org.scalatest._
import play.api.inject.guice._

/**
 * Trait that provides a new `Application` instance for each test.
 * 
 * This `SuiteMixin` trait's overridden `withFixture` method creates a new `Application`
 * before each test and ensures it is cleaned up after the test has completed. You can
 * access the `Application` from your tests as method `app` (which is marked implicit).
 *
 * By default, this trait creates a new `Application` for each test using default parameter values, which
 * is returned by the `newAppForTest` method defined in this trait. If your tests need a `Application` with non-default
 * parameters, override `newAppForTest` to return it.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneapppertest
 *
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * class ExampleSpec extends PlaySpec with OneAppPerTest {
 *
 *   // Override newAppForTest if you need a FakeApplication with other than non-default parameters.
 *   implicit override def newAppForTest(testData: TestData): Application =
 *     new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
 *
 *   "The OneAppPerTest trait" must {
 *     "provide an Application" in {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *   }
 * }
 * </pre>
 */
trait OneAppPerTest extends TestSuiteMixin { this: TestSuite =>

  /**
   * Creates new instance of `Application` with parameters set to their defaults. Override this method if you
   * need a `Application` created with non-default parameter values.
   */
  def newAppForTest(testData: TestData): Application = new GuiceApplicationBuilder().build()
  private var appPerTest: Application = _

  /**
   * Implicit method that returns the `Application` instance for the current test.
   */
  implicit final def app: Application = synchronized { appPerTest }

  /**
   * Creates a new `Application` instance before executing each test, and
   * ensure it is cleaned up after the test completes. You can access the `Application` from
   * your tests via `app`.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized { appPerTest = newAppForTest(test) }
    Helpers.running(app) {
      super.withFixture(test)
    } 
  } 
}


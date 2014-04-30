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
 * Trait that provides a new `FakeApplication` and running `TestServer` instance for each test executed in a ScalaTest `Suite`.
 * 
 * This `SuiteMixin` trait overrides ScalaTest's `withFixture` method to create a new `FakeApplication` and `TestServer`
 * before each test, and ensure they are cleaned up after the test has completed. The `FakeApplication` is available (implicitly) from
 * method `app`. The `TestServer`'s port number is available as `port` (and implicitly available as `portNumber`, wrapped
 * in a [[org.scalatestplus.play.PortNumber PortNumber]]).
 *
 * By default, this trait creates a new `FakeApplication` for each test using default parameter values, which
 * is returned by the `newAppForTest` method defined in this trait. If your tests need a `FakeApplication` with non-default 
 * parameters, override `newAppForTest` to return it.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneserverpertest
 * 
 * import play.api.test._
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * 
 * class ExampleSpec extends PlaySpec with OneServerPerTest {
 * 
 *   // Override newAppForTest if you need a FakeApplication with other than non-default parameters.
 *   implicit override def newAppForTest(testData: TestData): FakeApplication =
 *     FakeApplication(additionalConfiguration = Map("ehcacheplugin" -> "disabled"))
 * 
 *   "The OneServerPerTest trait" must {
 *     "provide a FakeApplication" in {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     "provide the port number" in {
 *       port mustBe Helpers.testServerPort
 *     }
 *     "provide an actual running server" in {
 *       import Helpers._
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boum")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *   }
 * }
 * </pre>
 */
trait OneServerPerTest extends SuiteMixin with ServerProvider { this: Suite =>

  private var privateApp: FakeApplication = _

  /**
   * Implicit method that returns the `FakeApplication` instance for the current test.
   */
  implicit final def app: FakeApplication = synchronized { privateApp }

  /**
   * Creates new instance of `FakeApplication` with parameters set to their defaults. Override this method if you
   * need a `FakeApplication` created with non-default parameter values.
   */
  def newAppForTest(testData: TestData): FakeApplication = new FakeApplication()

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  lazy val port: Int = Helpers.testServerPort

  /**
   * Creates new `FakeApplication` and running `TestServer` instances before executing each test, and 
   * ensures they are cleaned up after the test completes. You can access the `FakeApplication` from
   * your tests as `app` and the `TestServer`'s port number as `port`.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized { privateApp = newAppForTest(test) }
    Helpers.running(TestServer(port, app)) {
      super.withFixture(test)
    }
  }
}


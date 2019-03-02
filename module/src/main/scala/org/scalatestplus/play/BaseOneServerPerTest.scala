/*
 * Copyright 2001-2016 Artima, Inc.
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

/**
 * Trait that provides a new `Application` and running `TestServer` instance for each test executed in a ScalaTest `Suite`.
 *
 * This `TestSuiteMixin` trait overrides ScalaTest's `withFixture` method to create a new `Application` and `TestServer`
 * before each test, and ensure they are cleaned up after the test has completed. The `Application` is available (implicitly) from
 * method `app`. The `TestServer`'s port number is available as `port` (and implicitly available as `portNumber`, wrapped
 * in a [[org.scalatestplus.play.PortNumber PortNumber]]).
 *
 * By default, this trait creates a new `Application` for each test using default parameter values, which
 * is returned by the `newAppForTest` method defined in this trait. If your tests need an `Application` with non-default
 * parameters, override `newAppForTest` to return it.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneserverpertest
 *
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 * import play.api.routing._
 *
 * class ExampleSpec extends PlaySpec with OneServerPerTest {
 *
 *   // Override newAppForTest or use GuiceOneServerPerTest
 *   implicit override def newAppForTest(testData: TestData): Application = new GuiceApplicationBuilder()
 *     .configure(Map("ehcacheplugin" -> "disabled"))
 *     .router(TestRoutes.router)
 *     .build()
 *
 *   "The OneServerPerTest trait" must {
 *     "provide a FakeApplication" in {
 *       app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "provide an http endpoint" in {
 *       runningServer.endpoints.httpEndpoint must not be empty
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
trait BaseOneServerPerTest extends TestSuiteMixin with ServerProvider { this: TestSuite with FakeApplicationFactory =>

  @volatile private var privateApp: Application = _
  @volatile private var privateServer: RunningServer = _

  private[this] val lock = new Object()

  /**
   * Implicit method that returns the `Application` instance for the current test.
   */
  implicit final def app: Application = {
    val a = privateApp
    if (a == null) { throw new IllegalStateException("Test isn't running yet so application is not available") }
    a
  }

  implicit final def runningServer: RunningServer = {
    val rs = privateServer
    if (rs == null) { throw new IllegalStateException("Test isn't running yet so the server endpoints are not available") }
    privateServer
  }

  /**
   * Creates new instance of `Application` with parameters set to their defaults. Override this method if you
   * need an `Application` created with non-default parameter values.
   */
  def newAppForTest(testData: TestData): Application = fakeApplication()

  protected def newServerForTest(app: Application, testData: TestData): RunningServer =
    DefaultTestServerFactory.start(app)

  /**
   * Creates new `Application` and running `TestServer` instances before executing each test, and
   * ensures they are cleaned up after the test completes. You can access the `Application` from
   * your tests as `app` and the `TestServer`'s port number as `port`.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    // Need to synchronize within a suite because we store current app/server in fields in the class
    // Could possibly pass app/server info in a ScalaTest object?
    lock.synchronized {
      privateApp = newAppForTest(test)
      privateServer = newServerForTest(app, test)
      try super.withFixture(test) finally {
        val rs = privateServer // Store before nulling fields
        privateApp = null
        privateServer = null
        // Stop server and release locks
        rs.stopServer.close()
      }
    }
  }
}


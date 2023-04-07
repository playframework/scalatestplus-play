/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalatestplus.play

import org.scalatest._
import play.api.Application
import play.api.test._

/**
 * Trait that provides a new `Application` and running `TestServer` instance per ScalaTest `Suite`.
 *
 * By default, this trait creates a new `Application` for the `Suite` using default parameter values, which
 * is made available via the `app` field defined in this trait and a new `TestServer` for the `Suite` using the port number provided by
 * its `port` field and the `Application` provided by its `app` field. If your `Suite` needs a
 * `Application` with non-default parameters, override `app`. If it needs a different port number,
 * override `port`.
 *
 * This `TestSuiteMixin` trait's overridden `run` method calls `start` on the `TestServer`
 * before executing the `Suite` via a call to `super.run`.
 * In addition, it places a reference to the `Application` provided by `app` into the `ConfigMap`
 * under the key `org.scalatestplus.play.app` and to the port number provided by `port` under the key
 * `org.scalatestplus.play.port`.  This allows any nested `Suite`s to access the `Suite`'s
 * `Application` and port number as well, most easily by having the nested `Suite`s mix in the
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredServer]] trait. On the status returned by `super.run`, this
 * trait's overridden `run` method registers a call to `stop` on the `TestServer` to be executed when the `Status`
 * completes, and returns the same `Status`. This ensure the `TestServer` will continue to execute until
 * all nested suites have completed, after which the `TestServer` will be stopped.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneserverpersuite
 *
 * import play.api.test._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * class ExampleSpec extends PlaySpec with OneServerPerSuite {
 *
 *   // Override fakeApplication() if you need a Application with other than non-default parameters.
 *   def fakeApplication(): Application =
 *     new GuiceApplicationBuilder().configure("ehcacheplugin" -> "disabled").build()
 *
 *   "The OneServerPerSuite trait" must {
 *     "provide an Application" in {
 *       app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in {
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
 *
 * If you have many tests that can share the same `Application` and `TestServer`, and you don't want to put them all into one
 * test class, you can place them into different `Suite` classes.
 * These will be your nested suites. Create a master suite that extends `OneServerPerSuite` and declares the nested
 * `Suite`s. Annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredServer`. Here's an example:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneserverpersuite
 *
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * // This is the "master" suite
 * class NestedExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with GuiceOneServerPerSuite {
 *   // Override app if you need an Application with other than non-default parameters.
 *   override def fakeApplication(): Application =
 *     new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
 * }
 *
 * // These are the nested suites
 *
 * @DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer
 * @DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredServer
 * @DoNotDiscover class RedSpec extends PlaySpec with ConfiguredServer
 * @DoNotDiscover
 * class BlueSpec extends PlaySpec with ConfiguredServer {
 *
 *   "The OneServerPerSuite trait" must {
 *     "provide an Application" in {
 *       app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in {
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
trait BaseOneServerPerSuite extends TestSuiteMixin with ServerProvider { this: TestSuite with FakeApplicationFactory =>

  /**
   * An implicit instance of `Application`.
   */
  implicit lazy val app: Application = fakeApplication()

  protected def testServerFactory: TestServerFactory = DefaultTestServerFactory

  protected implicit lazy val runningServer: RunningServer =
    testServerFactory.start(app)

  /**
   * Invokes `start` on a new `TestServer` created with the `Application` provided by `app` and the
   * port number defined by `port`, places the `Application` and port number into the `ConfigMap` under the keys
   * `org.scalatestplus.play.app` and `org.scalatestplus.play.port`, respectively, to make
   * them available to nested suites; calls `super.run`; and lastly ensures the `Application` and test server are stopped after
   * all tests and nested suites have completed.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    try {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app) + ("org.scalatestplus.play.port" -> port)
      val newArgs      = args.copy(configMap = newConfigMap)
      val status       = super.run(testName, newArgs)
      status.whenCompleted { _ =>
        runningServer.stopServer.close()
      }
      status
    } catch { // In case the suite aborts, ensure the server is stopped
      case ex: Throwable =>
        runningServer.stopServer.close()
        throw ex
    }
  }
}

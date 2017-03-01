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

import org.scalatest._
import play.api.Application

/**
 * Trait that provides a configured `Application` and server port number to the suite into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.guice.GuiceOneServerPerSuite GuiceOneServerPerSuite]]
 * to make use of the `Application` and port number provided by `OneServerPerSuite`. Trait `OneServerPerSuite` will ensure
 * the `Application` is placed in the `ConfigMap` under the key `org.scalatestplus.play.app` and the port number
 * under the key `org.scalatestplus.play.port` before nested suites are invoked. This information represents the "configured server" that
 * is passed from the enclosing suite to the nested suites. Trait `ConfiguredServer` extracts this information from
 * from the `ConfigMap` and makes the `Application` available via the `app` method, the port number available as an `Int` from
 * the `port` method, and also the port number wrapped in a [[org.scalatestplus.play.PortNumber PortNumber]] available as implicit method `portNumber` (for use
 * with trait [[org.scalatestplus.play.WsScalaTestClient WsScalaTestClient]]).
 *
 * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`. Here's an example,
 * taken from `GuiceOneAppPerSuite`'s documentation:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneserverpersuite
 *
 * import play.api.test._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite {
 *
 *   // Override fakeApplication() if you need a Application with other than non-default parameters.
 *   def fakeApplication(): Application =
 *     new GuiceApplicationBuilder().configure("ehcacheplugin" -> "disabled").build()
 *
 *   "The OneServerPerSuite trait" must {
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
trait ConfiguredServer extends TestSuiteMixin with ServerProvider { this: TestSuite =>

  private var configuredApp: Application = _

  /**
   * The "configured" `Application` instance that was passed into `run` via the `ConfigMap`.
   *
   * @return the configured `Application`
   */
  implicit final def app: Application = synchronized { configuredApp }

  private var configuredPort: Int = -1

  /**
   * The "configured" port number, passed into `run` via the `ConfigMap`, at which the `TestServer` is running.
   *
   * @return the configured port number
   */
  def port: Int = synchronized { configuredPort }

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.app" whose value is a `Application`,
   * and a key named "org.scalatestplus.play.port" whose value is an `Int`,
   * and if they exist, sets the `Application` as the value that will be returned from the `app` method and
   * the `Int` as the value that will be returned from the `port` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.app" in `args.configMap`, or the associated value is
   * not a `Application`, or if no key matches "org.scalatestplus.play.port" in `args.configMap`,
   * or the associated value is not an `Int`, throws `IllegalArgumentException`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws java.lang.IllegalArgumentException if the `Application` and/or port number does not appear in `args.configMap` under the expected keys
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[Application]("org.scalatestplus.play.app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new Exception("Trait ConfiguredServer needs an Application value associated with key \"org.scalatestplus.play.app\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    args.configMap.getOptional[Int]("org.scalatestplus.play.port") match {
      case Some(cp) => synchronized { configuredPort = cp }
      case None => throw new Exception("Trait ConfiguredServer needs an Int value associated with key \"org.scalatestplus.play.port\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


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
import play.api.test.RunningServer
import play.core.server.ServerEndpoint
import play.core.server.ServerEndpoints

/**
 * Trait that provides a configured `Application` and server port number to the suite into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.guice.GuiceOneServerPerSuite GuiceOneServerPerSuite]]
 * to make use of the `Application` and port number provided by `OneServerPerSuite`. Trait `OneServerPerSuite` will ensure
 * the `ServerProvider` is placed in the `ConfigMap` under the key `org.scalatestplus.play.server.provider` and the port number
 * under the key `org.scalatestplus.play.port` before nested suites are invoked. This information represents the "configured server" that
 * is passed from the enclosing suite to the nested suites. Trait `ConfiguredServer` extracts this information from
 * from the `ConfigMap` and makes the port number available as an `Int` from the `port` method,
 * and also the port number wrapped in a [[org.scalatestplus.play.PortNumber PortNumber]] available as implicit method `portNumber` (for use
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
trait ConfiguredServer
    extends ConfiguredApp
    with BeforeAndAfterAllConfigMap
    with BeforeAndAfterEachTestData
    with ServerProvider { this: Suite =>

  @volatile private var privateServer: RunningServer = _

  final implicit def runningServer: RunningServer = {
    require(privateServer != null, "Test isn't running yet so the server endpoints are not available")
    privateServer
  }

  private var _configuredPort: Int = -1

  /**
   * The "configured" port number, passed into `run` via the `ConfigMap`, at which the `TestServer` is running.
   *
   * @return the configured port number
   */
  protected final def configuredPort: Int = synchronized { _configuredPort }

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.provider" whose value is an `ServerProvider`,
   * and if they exist, sets the `ServerProvider` as the value that will be returned from the `port` method, then calls
   *
   * If no key matches "org.scalatestplus.play.provider" in `args.configMap`,
   * or the associated value is not a `ServerProvider`, throws `IllegalArgumentException`.
   *
   * @throws java.lang.IllegalArgumentException if the `Application` and/or port number does not appear in `args.configMap` under the expected keys
   */
  protected override def beforeAll(configMap: ConfigMap): Unit = {
    super.beforeAll(configMap)
    setServerFrom(configMap)
  }

  /**
   * Places a reference to the server into per-test instances
   */
  protected override def beforeEach(testData: TestData): Unit = {
    super.beforeEach(testData)
    if (isInstanceOf[OneInstancePerTest])
      setServerFrom(testData.configMap)
  }

  private def setServerFrom(configMap: ConfigMap): Unit = {
    configMap.getOptional[ServerProvider]("org.scalatestplus.play.server.provider") match {
      case Some(cp) =>
        synchronized {
          privateServer = cp.runningServer
          _configuredPort = cp.port
        }
      case None =>
        throw new Exception(
          "Trait ConfiguredServer needs an Int value associated with key \"org.scalatestplus.play.server.provider\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?"
        )
    }
  }

  /**
   * Places the server port into the test's ConfigMap
   */
  abstract override def testDataFor(testName: String, configMap: ConfigMap): TestData = {
    configMap.getOptional[ServerProvider]("org.scalatestplus.play.server.provider") match {
      //when running as OneInstancePerTest, we need to reuse the BeforeAll instance's server
      case Some(sp) => super.testDataFor(testName, configMap + ("org.scalatestplus.play.port" -> sp.port))
      case _        => super.testDataFor(testName, configMap + ("org.scalatestplus.play.port" -> port))
    }
  }
}

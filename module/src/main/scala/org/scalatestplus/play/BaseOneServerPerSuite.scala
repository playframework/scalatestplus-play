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
 * This `SuiteMixin` trait's overridden `run` method calls `start` on the `TestServer`
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
trait BaseOneServerPerSuite
    extends SuiteMixin
    with BeforeAndAfterAll
    with BeforeAndAfterEachTestData
    with ServerProvider {
  this: Suite with FakeApplicationFactory =>

  @volatile private var privateServer: RunningServer = _

  final implicit def runningServer: RunningServer = {
    require(privateServer != null, "Test isn't running yet so the server endpoints are not available")
    privateServer
  }

  /**
   * An implicit instance of `Application`.
   */
  final implicit def app: Application = {
    runningServer.app
  }

  protected override def beforeAll(): Unit = {
    privateServer = startTestServer
    super.beforeAll()
  }

  protected def startTestServer: RunningServer = DefaultTestServerFactory.start(fakeApplication())

  protected override def afterAll(): Unit = {
    try {
      super.afterAll()
    } finally {
      val server = runningServer
      privateServer = null
      server.stopServer.close()
    }
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
    synchronized { privateServer = providerFrom(configMap).runningServer }
  }

  private def providerFrom(configMap: ConfigMap): ServerProvider = {
    configMap
      .getOptional[ServerProvider]("org.scalatestplus.play.server.provider")
      .getOrElse(
        throw new IllegalArgumentException(
          "BaseOneServerPerSuite needs an Application value associated with key \"org.scalatestplus.play.server.provider\" in the config map"
        )
      )
  }

  /**
   * Places the server port into the test's ConfigMap
   */
  abstract override def testDataFor(testName: String, configMap: ConfigMap): TestData = {
    val serverProvider = providerFrom(configMap)
    val newConfigMap   = configMap + ("org.scalatestplus.play.app" -> serverProvider.app) + ("org.scalatestplus.play.port" -> serverProvider.port)
    super.testDataFor(testName, newConfigMap)
  }

  //put a provider into the config map(instead of server directly), so that if tests are excluded, the server is never created
  abstract override def run(testName: Option[String], args: Args): Status = {
    // if a test is running under OneInstancePerTest, the config map will already be set
    if (args.runTestInNewInstance) super.run(testName, args)
    else {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.server.provider" -> this)
      val newArgs      = args.copy(configMap = newConfigMap)
      super.run(testName, newArgs)
    }
  }

}

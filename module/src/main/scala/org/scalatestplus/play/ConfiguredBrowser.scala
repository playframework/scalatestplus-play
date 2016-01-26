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
import play.api.Play
import selenium.WebBrowser
import org.openqa.selenium.WebDriver
import concurrent.Eventually
import concurrent.IntegrationPatience
import BrowserFactory.UninitializedDriver

/**
 * Trait that provides a configured `Application`, server port number, and Selenium `WebDriver` to the suite
 * into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]]
 * to make use of the `WebDriver` provided by `OneBrowserPerSuite`. Trait `OneBrowserPerSuite` will ensure
 * the `WebDriver` is placed in the `ConfigMap` under the key `org.scalatestplus.play.webDriver` before nested suites are invoked. This
 * information represents the "configured browser" that is passed from the enclosing suite to the nested suites. Trait `ConfiguredBrowser` extracts this information from
 * from the `ConfigMap` and makes the `WebDriver` available implicitly from the `webDriver` method.
 *
 * This trait's self-type, [[org.scalatestplus.play.ServerProvider ServerProvider]],  will ensure 
 * a `TestServer` and `Application` are available to each test. The self-type will require that you mix in either
 * [[org.scalatestplus.play.OneServerPerSuite OneServerPerSuite]], [[org.scalatestplus.play.OneServerPerTest OneServerPerTest]], 
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredServer]] before you mix in this trait. Your choice among these three
 * `ServerProvider`s will determine the extent to which one or more `TestServer`s are shared by multiple tests.
 *
 * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`. Here's an example
 * taken from the documentation for trait [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]]:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.onebrowserpersuite
 *
 * import play.api.test.Helpers
 * import org.scalatest.tags.FirefoxBrowser
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 * import play.api.routing._
 *
 * @FirefoxBrowser
 * class ExampleSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory {
 *
 *   // Override app if you need a Application with other than non-default parameters.
 *   implicit override lazy val app: Application =
 *     new GuiceApplicationBuilder().configure("foo" -> "bar", "ehcacheplugin" -> "disabled").additionalRouter(Router.from(TestRoute)).build()
 *
 *   "The OneBrowserPerSuite trait" must {
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
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boum")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *     "provide a web driver" in {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 * }
 * </pre>
 */
trait ConfiguredBrowser extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite with ServerProvider => 

  private var configuredWebDriver: WebDriver = UninitializedDriver

  /**
   * The "configured" Selenium `WebDriver`, passed into `run` via the `ConfigMap`.
   *
   * @return the configured port number
   */
  implicit def webDriver: WebDriver = synchronized { configuredWebDriver } 

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.webDriver" whose value is a `WebDriver`,
   * and if it exists, sets the `WebDriver` as
   * the value that will be returned from the `webDriver` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.webDriver" in `args.configMap`,
   * or the associated value is not a `WebDriver`, throws `IllegalArgumentException`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws IllegalArgumentException if the `WebDriver` does not appear in `args.configMap` under the expected key
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[WebDriver]("org.scalatestplus.play.webDriver") match {
      case Some(cwd) => synchronized { configuredWebDriver = cwd }
      case None => throw new Exception("ConfiguredBrowser needs a WebDriver value associated with key \"org.scalatestplus.play.webDriver\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


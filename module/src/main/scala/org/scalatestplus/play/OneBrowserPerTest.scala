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
import selenium.WebBrowser
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import BrowserFactory.UnavailableDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver
import BrowserFactory.UninitializedDriver

/**
 * Trait that provides a new Selenium `WebDriver` instance for each test executed in a ScalaTest `Suite`.
 * 
 * This trait overrides ScalaTest's `withFixture` method to create a new `WebDriver` instance 
 * before each test, and ensure it is closed after the test has completed.
 * The `WebDriver` is available (implicitly) from method `webDriver`.
 *
 * This trait's self-type, [[org.scalatestplus.play.ServerProvider ServerProvider]],  will ensure 
 * a `TestServer` and `Application` are available to each test. The self-type will require that you mix in either
 * [[org.scalatestplus.play.OneServerPerSuite OneServerPerSuite]], [[org.scalatestplus.play.OneServerPerTest OneServerPerTest]], 
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredServer]] before you mix in this trait. Your choice among these three
 * `ServerProvider`s will determine the extent to which one or more `TestServer`s are shared by multiple tests.
 *
 * Here's an example that shows demonstrates of the services provided by this trait. Note that
 * to use this trait, you must mix in one of the driver factories (this example
 * mixes in [[org.scalatestplus.play.FirefoxFactory FirefoxFactory]]):
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.onebrowserpertest
 *
 * import play.api.test._
 * import org.scalatest._
 * import org.scalatest.tags.FirefoxBrowser
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 * import play.api.routing._
 *
 * @FirefoxBrowser
 * class ExampleSpec extends PlaySpec with OneServerPerTest with OneBrowserPerTest with FirefoxFactory {
 *
 *   // Override newAppForTest if you need an Application with other than non-default parameters.
 *   override def newAppForTest(testData: TestData): Application =
 *     new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).additionalRouter(Router.from(TestRoute)).build()
 *
 *   "The OneBrowserPerTest trait" must {
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
trait OneBrowserPerTest extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience with BrowserFactory { this: Suite with ServerProvider =>

  private var privateWebDriver: WebDriver = UninitializedDriver

  /**
   * Implicit method to get the `WebDriver` for the current test.
   */
  implicit def webDriver: WebDriver = synchronized { privateWebDriver }

  /**
   * Creates a new instance of `WebDriver` before 
   * running each test, ensuring it is closed after the test completes. 
   * If an error occurs when attempting to creat the `WebDriver`, [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] 
   * will be used instead and all tests will be canceled automatically.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized {
      privateWebDriver = createWebDriver()
    }
    try {
      privateWebDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ => super.withFixture(test)
      }
    }
    finally {
      privateWebDriver match {
        case _: UnavailableDriver => // do nothing
        case safariDriver: SafariDriver => safariDriver.quit()
        case chromeDriver: ChromeDriver => chromeDriver.quit()
        case _ => privateWebDriver.close()
      }
    }
  }
}


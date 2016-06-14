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
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import org.scalatest._
import fixture._
import selenium.WebBrowser
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import BrowserFactory.UnavailableDriver
import org.openqa.selenium.safari.SafariDriver

/**
 * Trait that helps you provide different fixtures to different tests: a `Application`, a `TestServer`, or one
 * of the Selenium `WebDrivers`s.
 *
 * Trait `MixedFixtures` can be mixed into any `fixture.Suite`. For convenience it is
 * mixed into [[org.scalatestplus.play.MixedPlaySpec MixedPlaySpec]]. In a `fixture.Suite`, tests can
 * take a no-arg function. `MixedFixtures` provides several no-arg function classes (classes extending `Function0`) that 
 * can be used to provide different fixtures for different tests.
 *
 * If a test needs a `Application`, use the `App` function, like this:
 *
 * <pre class="stHighlight">
 * "provide an Application" in new App(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *   app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 * }
 * </pre>
 *
 * If a test needs an `Application` and running `TestServer`, use the `Server` function, like this:
 *
 * <pre class="stHighlight">
 * "send 404 on a bad request" in new Server {
 *   import java.net._
 *   val url = new URL("http://localhost:" + port + "/boom")
 *   val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *   try con.getResponseCode mustBe 404
 *   finally con.disconnect()
 * }
 * </pre>
 * 
 * If a test needs an `Application`, running `TestServer`, and Selenium driver, use
 * one of functions `Chrome`, `Firefox`, `HtmlUnit`, `InternetExplorer`, or `Safari`.
 * If the chosen Selenium driver is unavailable on the host platform, the test will
 * be automatically canceled. Here's an example that uses the `Safari` function:
 * 
 * <pre class="stHighlight">
 * "provide a web driver" in new Safari(fakeApp()) {
 *   go to ("http://localhost:" + port + "/testing")
 *   pageTitle mustBe "Test Page"
 *   click on find(name("b")).value
 *   eventually { pageTitle mustBe "scalatest" }
 * }
 * </pre>
 * 
 * Here's a complete example:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.mixedfixtures
 *
 * import play.api.test._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 * import play.api.routing._
 *
 * class ExampleSpec extends MixedPlaySpec {
 *
 *   // Some helper methods
 *   def buildApp[A](elems: (String, String)*) = new GuiceApplicationBuilder()
 *     .configure(Map(elems:_*))
 *     .router(Router.from(TestRoute))
 *     .build()
 *
 *   def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
 *
 *   "The App function" must {
 *     "provide an Application" in new App(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new App(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new App(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *   }
 *   "The Server function" must {
 *     "provide an Application" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Server {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boom")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *   }
 *   "The HtmlUnit function" must {
 *     "provide an Application" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new HtmlUnit {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boom")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *     "provide a web driver" in new HtmlUnit(buildApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *   "The Firefox function" must {
 *     "provide an Application" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Firefox {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boom")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *     "provide a web driver" in new Firefox(buildApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *   "The Safari function" must {
 *     "provide an Application" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Safari {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boom")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *     "provide a web driver" in new Safari(buildApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *   "The Chrome function" must {
 *     "provide an Application" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Chrome {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boom")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *     "provide a web driver" in new Chrome(buildApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *   "The InternetExplorer function" must {
 *     "provide an Application" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the Application" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new InternetExplorer {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boom")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *     "provide a web driver" in new InternetExplorer(buildApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *   "Any old thing" must {
 *     "be doable without much boilerplate" in { () =>
 *        1 + 1 mustEqual 2
 *      }
 *   }
 * }
 * </pre>
 */
trait MixedFixtures extends TestSuiteMixin with UnitFixture { this: fixture.TestSuite =>

  /**
   * `NoArg` subclass that provides an `Application` fixture.
   */
  abstract class App(appFun: => Application = (new GuiceApplicationBuilder()).build()) extends NoArg {
    /**
     * Makes the passed-in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Runs the passed in `Application` before executing the test body, ensuring it is closed after the test body completes.
     */
    override def apply() {
      def callSuper = super.apply()  // this is needed for Scala 2.10 to work
      Helpers.running(app)(callSuper)
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of a `Application` and running `TestServer`.
   */
  abstract class Server(appFun: => Application = (new GuiceApplicationBuilder()).build(), val port: Int = Helpers.testServerPort) extends NoArg {
    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and  port before executing the
     * test body, ensuring both are stopped after the test body completes.
     */
    override def apply() {
      def callSuper = super.apply()  // this is needed for Scala 2.10 to work
      Helpers.running(TestServer(port, app))(callSuper)
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `HtmlUnitDriver`.
   */
  abstract class HtmlUnit(appFun: => Application = (new GuiceApplicationBuilder()).build(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with HtmlUnitFactory {
    /**
     * A lazy implicit instance of `HtmlUnitDriver`. It will hold `UnavailableDriver` if `HtmlUnitDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `HtmlUnitDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.close()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of a `Application`, running `TestServer`, and
   * Selenium `FirefoxDriver`.
   */
  abstract class Firefox(appFun: => Application = (new GuiceApplicationBuilder()).build(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with FirefoxFactory {

    /**
     * A lazy implicit instance of `FirefoxDriver`, it will hold `UnavailableDriver` if `FirefoxDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `FirefoxDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.close()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `SafariDriver`.
   */
  abstract class Safari(appFun: => Application = (new GuiceApplicationBuilder()).build(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with SafariFactory {
    /**
     * A lazy implicit instance of `SafariDriver`, it will hold `UnavailableDriver` if `SafariDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `SafariDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.quit()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `ChromeDriver`.
   */
  abstract class Chrome(appFun: => Application = (new GuiceApplicationBuilder()).build(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with ChromeFactory {
    /**
     * A lazy implicit instance of `ChromeDriver`, it will hold `UnavailableDriver` if `ChromeDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `ChromeDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.quit()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `InternetExplorerDriver`.
   */
  abstract class InternetExplorer(appFun: => Application = (new GuiceApplicationBuilder()).build(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with InternetExplorerFactory {
    /**
     * A lazy implicit instance of `InternetExplorerDriver`, it will hold `UnavailableDriver` if `InternetExplorerDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `InternetExplorerDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.close()
      }
    }
  }
}


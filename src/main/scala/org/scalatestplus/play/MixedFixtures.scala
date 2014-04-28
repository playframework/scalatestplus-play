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
import fixture._
import selenium.WebBrowser
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import BrowserFactory.NoDriver
import org.openqa.selenium.safari.SafariDriver

/**
 * Trait that helps you provide different fixtures to different tests: a `FakeApplication`, a `TestServer`, or one
 * of the Selenium `WebBrowser`s.
 *
 * Trait `MixedFixtures` can be mixed into any `fixture.Suite`. For convenience it is
 * mixed into [[org.scalatestplus.play.MixedPlaySpec MixedPlaySpec]]. In a `fixture.Suite`, tests can
 * take a no-arg function. `MixedFixtures` provides several no-arg function classes (classes extending `Function0`) that 
 * can be used to provide different fixtures for different tests.
 *
 * If a test needs a `FakeApplication`, use the `App` function, like this:
 *
 * <pre class="stHighlight">
 * "provide a FakeApplication" in new App(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *   app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 * }
 * </pre>
 *
 * If a test needs a `FakeApplication` and running `TestServer`, use the `Server` function, like this:
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
 * If a test needs a `FakeApplication`, running `TestServer`, and Selenium driver, use
 * one of functions `Chrome`, `Firefox`, `HtmlUnit`, `InternetExplorer`, or `Safari`:
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
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * 
 * class ExampleSpec extends MixedPlaySpec {
 * 
 *   // Some helper methods
 *   def fakeApp[A](elems: (String, String)*) = FakeApplication(additionalConfiguration = Map(elems:_*), withRoutes = TestRoute)
 *   def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
 * 
 *   // If a test just needs a <code>FakeApplication</code>, use "<code>new App</code>":
 *   "The App function" must {
 *     "provide a FakeApplication" in new App(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new App(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new App(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *   }
 *
 *   // If a test needs a <code>FakeApplication</code> and running <code>TestServer</code>, use "<code>new Server</code>":
 *   "The Server function" must {
 *     "provide a FakeApplication" in new Server(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new Server(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new Server(fakeApp("ehcacheplugin" -&gt; "disabled")) {
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
 *
 *   // If a test needs a <code>FakeApplication</code>, running <code>TestServer</code>, and Selenium
 *   // <code>HtmlUnit</code> driver use "<code>new HtmlUnit</code>":
 *   "The HtmlUnit function" must {
 *     "provide a FakeApplication" in new HtmlUnit(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new HtmlUnit(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new HtmlUnit(fakeApp("ehcacheplugin" -&gt; "disabled")) {
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
 *     "provide a web driver" in new HtmlUnit(fakeApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *
 *   // If a test needs a <code>FakeApplication</code>, running <code>TestServer</code>, and Selenium
 *   // <code>Firefox</code> driver use "<code>new Firefox</code>":
 *   "The Firefox function" must {
 *     "provide a FakeApplication" in new Firefox(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new Firefox(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new Firefox(fakeApp("ehcacheplugin" -&gt; "disabled")) {
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
 *     "provide a web driver" in new Firefox(fakeApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *
 *   // If a test needs a <code>FakeApplication</code>, running <code>TestServer</code>, and Selenium
 *   // <code>Safari</code> driver use "<code>new Safari</code>":
 *   "The Safari function" must {
 *     "provide a FakeApplication" in new Safari(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new Safari(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new Safari(fakeApp("ehcacheplugin" -&gt; "disabled")) {
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
 *     "provide a web driver" in new Safari(fakeApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *
 *   // If a test needs a <code>FakeApplication</code>, running <code>TestServer</code>, and Selenium
 *   // <code>Chrome</code> driver use "<code>new Chrome</code>":
 *   "The Chrome function" must {
 *     "provide a FakeApplication" in new Chrome(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new Chrome(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new Chrome(fakeApp("ehcacheplugin" -&gt; "disabled")) {
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
 *     "provide a web driver" in new Chrome(fakeApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *
 *   // If a test needs a <code>FakeApplication</code>, running <code>TestServer</code>, and Selenium
 *   // <code>InternetExplorer</code> driver use "<code>new InternetExplorer</code>":
 *   "The InternetExplorer function" must {
 *     "provide a FakeApplication" in new InternetExplorer(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in new InternetExplorer(fakeApp("ehcacheplugin" -&gt; "disabled")) {
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in new InternetExplorer(fakeApp("ehcacheplugin" -&gt; "disabled")) {
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
 *     "provide a web driver" in new InternetExplorer(fakeApp()) {
 *       go to ("http://localhost:" + port + "/testing")
 *       pageTitle mustBe "Test Page"
 *       click on find(name("b")).value
 *       eventually { pageTitle mustBe "scalatest" }
 *     }
 *   }
 *
 *   // If a test does not need any special fixtures, just 
 *   // write <code>"in { () => ..."</code>
 *   "Any old thing" must {
 *     "be doable without much boilerplate" in { () =>
 *        1 + 1 mustEqual 2
 *      }
 *   }
 * }
 * </pre>
 */
trait MixedFixtures extends SuiteMixin with UnitFixture { this: fixture.Suite =>

  /**
   * Class that provides fixture for `FakeApplication`, it will run the passed in `FakeApplication` 
   * before running the test.
   */
  abstract class App(val app: FakeApplication = FakeApplication()) extends NoArg {
    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Override to run the passed in `FakeApplication` first before running the test.
     */
    override def apply() {
      Helpers.running(app)(super.apply())
    }
  }

  /**
   * Class that provides fixture for `TestServer` and `FakeApplication`, 
   * it will run the passed in `TestServer` before running the test.
   */
  abstract class Server(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends NoArg {
    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a `TestServer` using the passed in `port` before running the test.
     */
    override def apply() {
      Helpers.running(TestServer(port, app))(super.apply())
    }
  }

  /**
   * Class that provides fixture for `HtmlUnit` browser.
   */
  abstract class HtmlUnit(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with HtmlUnitFactory {
    /**
     * A lazy implicit instance of `HtmlUnitDriver`, it will hold `NoDriver` if `HtmlUnitDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a `TestServer` using the passed in `port` before running the test, 
     * and close the `HtmlUnitDriver` automatically after test execution.
     */
    override def apply() {
      webDriver match {
        case NoDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ => 
          try Helpers.running(TestServer(port, app))(super.apply())
          finally webDriver.close()
      }
    }
  }

  /**
   * Class that provides fixture for `Firefox` browser.
   */
  abstract class Firefox(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with FirefoxFactory {

    /**
     * A lazy implicit instance of `FirefoxDriver`, it will hold `NoDriver` if `FirefoxDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a `TestServer` using the passed in `port` before running the test, 
     * and close the `FirefoxDriver` automatically after test execution.
     */
    override def apply() {
      webDriver match {
        case NoDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ => 
          try Helpers.running(TestServer(port, app))(super.apply())
          finally webDriver.close()
      }
    }
  }

  /**
   * Class that provides fixture for `Safari` browser.
   */
  abstract class Safari(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with SafariFactory {
    /**
     * A lazy implicit instance of `SafariDriver`, it will hold `NoDriver` if `SafariDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a `TestServer` using the passed in `port` before running the test, 
     * and close the `SafariDriver` automatically after test execution.
     */
    override def apply() {
      webDriver match {
        case NoDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ => 
          try Helpers.running(TestServer(port, app))(super.apply())
          finally webDriver.quit()
      }
    }
  }

  /**
   * Class that provides fixture for `Chrome` browser.
   */
  abstract class Chrome(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with ChromeFactory {
    /**
     * A lazy implicit instance of `ChromeDriver`, it will hold `NoDriver` if `ChromeDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a `TestServer` using the passed in `port` before running the test, 
     * and close the `ChromeDriver` automatically after test execution.
     */
    override def apply() {
      webDriver match {
        case NoDriver(ex, errorMessage) =>
          val msg = Resources("cantCreateChromeDriver")
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ => 
          try Helpers.running(TestServer(port, app))(super.apply())
          finally webDriver.quit()
      }
    }
  }

  /**
   * Class that provides fixture for `InternetExplorer` browser.
   */
  abstract class InternetExplorer(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with InternetExplorerFactory {
    /**
     * A lazy implicit instance of `InternetExplorerDriver`, it will hold `NoDriver` if `InternetExplorerDriver` 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in `FakeApplication` implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a `TestServer` using the passed in `port` before running the test, 
     * and close the `InternetExplorerDriver` automatically after test execution.
     */
    override def apply() {
      webDriver match {
        case NoDriver(ex, errorMessage) =>
          val msg = Resources("cantCreateInternetExplorerDriver")
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ => 
          try Helpers.running(TestServer(port, app))(super.apply())
          finally webDriver.close()
      }
    }
  }
}


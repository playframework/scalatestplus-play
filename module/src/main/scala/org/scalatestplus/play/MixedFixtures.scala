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

import org.openqa.selenium.WebDriver
import org.scalatest._
import org.scalatest.fixture._
import org.scalatest.FixtureTestSuite
import org.scalatestplus.selenium.WebBrowser
import org.scalatestplus.play.BrowserFactory.UnavailableDriver
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.core.PlayVersion

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
 *   override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 * }
 * </pre>
 *
 * If a test needs an `Application` and running `TestServer`, use the `Server` function, like this:
 *
 * <pre class="stHighlight">
 * "send 404 on a bad request" in new Server {
 *   override def running() = {
 *     import java.net._
 *     val url = new URL("http://localhost:" + port + "/boom")
 *     val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *     try con.getResponseCode mustBe 404
 *     finally con.disconnect()
 *   }
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
 *   override def running() = {
 *     go to ("http://localhost:" + port + "/testing")
 *     pageTitle mustBe "Test Page"
 *     click on find(name("b")).value
 *     eventually { pageTitle mustBe "scalatest" }
 *   }
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
 *     .router(TestRoutes.router)
 *     .build()
 *
 *   def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
 *
 *   "The App function" must {
 *     "provide an Application" in new App(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new App(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *   }
 *   "The Server function" must {
 *     "provide an Application" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Server {
 *       override def running() = {
 *         import java.net._
 *         val url = new URL("http://localhost:" + port + "/boom")
 *         val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *         try con.getResponseCode mustBe 404
 *         finally con.disconnect()
 *       }
 *     }
 *   }
 *   "The HtmlUnit function" must {
 *     "provide an Application" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new HtmlUnit {
 *       override def running() = {
 *         import java.net._
 *         val url = new URL("http://localhost:" + port + "/boom")
 *         val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *         try con.getResponseCode mustBe 404
 *         finally con.disconnect()
 *       }
 *     }
 *     "provide a web driver" in new HtmlUnit(buildApp()) {
 *       override def running() = {
 *         go to ("http://localhost:" + port + "/testing")
 *         pageTitle mustBe "Test Page"
 *         click on find(name("b")).value
 *         eventually { pageTitle mustBe "scalatest" }
 *       }
 *     }
 *   }
 *   "The Firefox function" must {
 *     "provide an Application" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Firefox {
 *       override def running() = {
 *         import java.net._
 *         val url = new URL("http://localhost:" + port + "/boom")
 *         val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *         try con.getResponseCode mustBe 404
 *         finally con.disconnect()
 *       }
 *     }
 *     "provide a web driver" in new Firefox(buildApp()) {
 *       override def running() = {
 *         go to ("http://localhost:" + port + "/testing")
 *         pageTitle mustBe "Test Page"
 *         click on find(name("b")).value
 *         eventually { pageTitle mustBe "scalatest" }
 *       }
 *     }
 *   }
 *   "The Safari function" must {
 *     "provide an Application" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Safari {
 *       override def running() = {
 *         import java.net._
 *         val url = new URL("http://localhost:" + port + "/boom")
 *         val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *         try con.getResponseCode mustBe 404
 *         finally con.disconnect()
 *       }
 *     }
 *     "provide a web driver" in new Safari(buildApp()) {
 *       override def running() = {
 *         go to ("http://localhost:" + port + "/testing")
 *         pageTitle mustBe "Test Page"
 *         click on find(name("b")).value
 *         eventually { pageTitle mustBe "scalatest" }
 *       }
 *     }
 *   }
 *   "The Chrome function" must {
 *     "provide an Application" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new Chrome {
 *       override def running() = {
 *         import java.net._
 *         val url = new URL("http://localhost:" + port + "/boom")
 *         val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *         try con.getResponseCode mustBe 404
 *         finally con.disconnect()
 *       }
 *     }
 *     "provide a web driver" in new Chrome(buildApp()) {
 *       override def running() = {
 *         go to ("http://localhost:" + port + "/testing")
 *         pageTitle mustBe "Test Page"
 *         click on find(name("b")).value
 *         eventually { pageTitle mustBe "scalatest" }
 *       }
 *     }
 *   }
 *   "The InternetExplorer function" must {
 *     "provide an Application" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
 *       override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     import Helpers._
 *     "send 404 on a bad request" in new InternetExplorer {
 *       override def running() = {
 *         import java.net._
 *         val url = new URL("http://localhost:" + port + "/boom")
 *         val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *         try con.getResponseCode mustBe 404
 *         finally con.disconnect()
 *       }
 *     }
 *     "provide a web driver" in new InternetExplorer(buildApp()) {
 *       override def running() = {
 *         go to ("http://localhost:" + port + "/testing")
 *         pageTitle mustBe "Test Page"
 *         click on find(name("b")).value
 *         eventually { pageTitle mustBe "scalatest" }
 *       }
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
trait MixedFixtures extends TestSuiteMixin with fixture.UnitFixture { this: FixtureTestSuite =>

  abstract class NoArgHelper(helperClass: Class[_]) extends NoArg {

    lazy val errorMsg: String = s"""
                                   |
                                   |For Scala 3 you need to wrap the body of ${helperClass.getSimpleName} in an `override def running() = ...` method:
                                   |
                                   |// Old:
                                   |new ${helperClass.getSimpleName}() {
                                   |  <code>
                                   |}
                                   |
                                   |// New:
                                   |new ${helperClass.getSimpleName}() {
                                   |  override def running() = {
                                   |    <code>
                                   |  }
                                   |}
                                   |
                                   |""".stripMargin

    if (
      PlayVersion.scalaVersion
        .startsWith("3") && this.getClass.getMethod("running").getDeclaringClass == classOf[NoArgHelper]
    ) {
      throw new NotImplementedError(errorMsg)
    }

    def running(): Unit = throw new NotImplementedError(errorMsg)

    final def callRunning() =
      PlayVersion.scalaVersion
        .startsWith("3") || this.getClass.getMethod("running").getDeclaringClass != classOf[NoArgHelper]

  }

  /**
   * `NoArg` subclass that provides an `Application` fixture.
   */
  abstract class App(appFun: => Application = new GuiceApplicationBuilder().build()) extends NoArgHelper(classOf[App]) {

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
    override def apply(): Unit = {
      if (callRunning()) {
        Helpers.running(app)(running())
      } else {
        def callSuper: Unit = super.apply() // this is needed for Scala 2.10 to work
        Helpers.running(app)(callSuper)
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of a `Application` and running `TestServer`.
   */
  abstract class Server(
      appFun: => Application = new GuiceApplicationBuilder().build(),
      val httpPort: Int = Helpers.testServerPort
  ) extends NoArgHelper(classOf[Server]) {

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    private var testServer: Option[TestServer] = None

    lazy val port: Int = testServer.flatMap(ts => ts.runningHttpPort).getOrElse(httpPort)

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and  port before executing the
     * test body, ensuring both are stopped after the test body completes.
     */
    override def apply(): Unit = {
      testServer = Some(TestServer(httpPort, app))
      if (callRunning()) {
        testServer.foreach(Helpers.running(_)(running()))
      } else {
        def callSuper: Unit = super.apply() // this is needed for Scala 2.10 to work
        testServer.foreach(Helpers.running(_)(callSuper))
      }
      testServer = None
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `HtmlUnitDriver`.
   */
  abstract class HtmlUnit(
      appFun: => Application = new GuiceApplicationBuilder().build(),
      val httpPort: Int = Helpers.testServerPort
  ) extends NoArgHelper(classOf[HtmlUnit])
      with WebBrowser
      with HtmlUnitFactory {

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

    private var testServer: Option[TestServer] = None

    lazy val port: Int = testServer.flatMap(ts => ts.runningHttpPort).getOrElse(httpPort)

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
    override def apply(): Unit = {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None    => cancel(errorMessage)
          }
        case _ =>
          if (callRunning()) {
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(running()))
            } finally {
              webDriver.quit()
              testServer = None
            }
          } else {
            def callSuper = super.apply() // this is needed for Scala 2.10 to work
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(callSuper))
            } finally {
              webDriver.quit()
              testServer = None
            }
          }
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of a `Application`, running `TestServer`, and
   * Selenium `FirefoxDriver`.
   */
  abstract class Firefox(
      appFun: => Application = new GuiceApplicationBuilder().build(),
      val httpPort: Int = Helpers.testServerPort
  ) extends NoArgHelper(classOf[Firefox])
      with WebBrowser
      with FirefoxFactory {

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

    private var testServer: Option[TestServer] = None

    lazy val port: Int = testServer.flatMap(ts => ts.runningHttpPort).getOrElse(httpPort)

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
    override def apply(): Unit = {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None    => cancel(errorMessage)
          }
        case _ =>
          if (callRunning()) {
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(running()))
            } finally {
              webDriver.quit()
              testServer = None
            }
          } else {
            def callSuper = super.apply() // this is needed for Scala 2.10 to work
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(callSuper))
            } finally {
              webDriver.quit()
              testServer = None
            }
          }
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `SafariDriver`.
   */
  abstract class Safari(
      appFun: => Application = new GuiceApplicationBuilder().build(),
      val httpPort: Int = Helpers.testServerPort
  ) extends NoArgHelper(classOf[Safari])
      with WebBrowser
      with SafariFactory {

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

    private var testServer: Option[TestServer] = None

    lazy val port: Int = testServer.flatMap(ts => ts.runningHttpPort).getOrElse(httpPort)

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
    override def apply(): Unit = {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None    => cancel(errorMessage)
          }
        case _ =>
          if (callRunning()) {
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(running()))
            } finally {
              webDriver.quit()
              testServer = None
            }
          } else {
            def callSuper = super.apply() // this is needed for Scala 2.10 to work
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(callSuper))
            } finally {
              webDriver.quit()
              testServer = None
            }
          }
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `ChromeDriver`.
   */
  abstract class Chrome(
      appFun: => Application = new GuiceApplicationBuilder().build(),
      val httpPort: Int = Helpers.testServerPort
  ) extends NoArgHelper(classOf[Chrome])
      with WebBrowser
      with ChromeFactory {

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

    private var testServer: Option[TestServer] = None

    lazy val port: Int = testServer.flatMap(ts => ts.runningHttpPort).getOrElse(httpPort)

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
    override def apply(): Unit = {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None    => cancel(errorMessage)
          }
        case _ =>
          if (callRunning()) {
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(running()))
            } finally {
              webDriver.quit()
              testServer = None
            }
          } else {
            def callSuper = super.apply() // this is needed for Scala 2.10 to work
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(callSuper))
            } finally {
              webDriver.quit()
              testServer = None
            }
          }
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `InternetExplorerDriver`.
   */
  abstract class InternetExplorer(
      appFun: => Application = new GuiceApplicationBuilder().build(),
      val httpPort: Int = Helpers.testServerPort
  ) extends NoArgHelper(classOf[InternetExplorer])
      with WebBrowser
      with InternetExplorerFactory {

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

    private var testServer: Option[TestServer] = None

    lazy val port: Int = testServer.flatMap(ts => ts.runningHttpPort).getOrElse(httpPort)

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
    override def apply(): Unit = {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None    => cancel(errorMessage)
          }
        case _ =>
          if (callRunning()) {
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(running()))
            } finally {
              webDriver.quit()
              testServer = None
            }
          } else {
            def callSuper = super.apply() // this is needed for Scala 2.10 to work
            try {
              testServer = Some(TestServer(httpPort, app))
              testServer.foreach(Helpers.running(_)(callSuper))
            } finally {
              webDriver.quit()
              testServer = None
            }
          }
      }
    }
  }
}

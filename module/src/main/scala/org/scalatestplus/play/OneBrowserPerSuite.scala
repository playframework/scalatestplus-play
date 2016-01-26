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

/* TODO: Make ConfiguredBrowser require a ServerProvider also, I think. */

/**
 * Trait that provides a new Selenium `WebDriver` instance per ScalaTest `Suite`.
 * 
 * This `SuiteMixin` trait's overridden `run` method 
 * places a reference to the `WebDriver` provided by `webDriver` under the key `org.scalatestplus.play.webDriver`.
 * This allows any nested `Suite`s to access the `Suite`'s 
 * `WebDriver` as well, most easily by having the nested `Suite`s mix in the
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredBrowser]] trait. On the status returned by `super.run`, this
 * trait's overridden `run` method registers a block of code to close the `WebDriver` to be executed when the `Status`
 * completes, and returns the same `Status`. This ensures the `WebDriver` will continue to be available until
 * all nested suites have completed, after which the `WebDriver` will be closed.
 * This trait also overrides `Suite.withFixture` to cancel tests automatically if the related
 * `WebDriver` is not available on the host platform.
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
 *
 * If you have many tests that can share the same `Application`, `TestServer`, and `WebDriver`, and you don't want to put them all into one
 * test class, you can place them into different "nested" `Suite` classes.
 * Create a master suite that extends `OneServerPerSuite` and declares the nested 
 * `Suite`s. Annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredBrowser`. Here's an example:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.onebrowserpersuite
 * 
 * import play.api.test._
 * import org.scalatest._
 * import tags.FirefoxBrowser
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * 
 * // This is the "master" suite
 * class NestedExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory {
 *   // Override app if you need a Application with other than non-default parameters.
 *   implicit override lazy val app: Application =
 *     FakeApplication(
 *       additionalConfiguration = Map("ehcacheplugin" -&gt; "disabled"),
 *       withRoutes = TestRoute
 *     )
 * }
 *  
 * // These are the nested suites
 * @DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser
 * @DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser
 * @DoNotDiscover class RedSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser
 * 
 * @DoNotDiscover
 * class BlueSpec extends PlaySpec with ConfiguredServer with ConfiguredBrowser {
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
 * It is possible to use `OneBrowserPerSuite` to run the same tests in more than one browser. Nevertheless,
 * you should consider the approach taken by [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]]
 * and [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]]
 * instead, as it requires a bit less boilerplate code than `OneBrowserPerSuite` to test in multiple browsers. 
 * If you prefer to use `OneBrowserPerSuite`, however, simply place your tests in an abstract superclass, then define concrete subclasses
 * for each browser you wish to test against. Here's an example:
 * 
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.onebrowserpersuite
 * 
 * import play.api.test._
 * import org.scalatest._
 * import tags._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * 
 * // Place your tests in an abstract class
 * abstract class MultiBrowserExampleSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite {
 * 
 *   // Override app if you need an Application with other than non-default parameters.
 *   implicit override lazy val app: Application =
 *     FakeApplication(
 *       additionalConfiguration = Map("ehcacheplugin" -> "disabled"),
 *       withRoutes = TestRoute
 *     )
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
 * 
 * // Then make a subclass that mixes in the factory for each
 * // Selenium driver you want to test with.
 * @FirefoxBrowser class FirefoxExampleSpec extends MultiBrowserExampleSpec with FirefoxFactory
 * @SafariBrowser class SafariExampleSpec extends MultiBrowserExampleSpec with SafariFactory
 * @InternetExplorerBrowser class InternetExplorerExampleSpec extends MultiBrowserExampleSpec with InternetExplorerFactory
 * @ChromeBrowser class ChromeExampleSpec extends MultiBrowserExampleSpec with ChromeFactory
 * @HtmlUnitBrowser class HtmlUnitExampleSpec extends MultiBrowserExampleSpec with HtmlUnitFactory
 * </pre>
 *
 * The concrete subclasses include tag annotations describing the browser used to make it
 * easier to include or exclude browsers in specific runs. This is not strictly necessary since if a browser is not supported 
 * on the host platform the tests will be automatically canceled. For example, here's how the output would look
 * if you ran the above tests on a platform that did not support Selenium drivers for Chrome or Internet Explorer
 * using sbt:
 *
 * <pre class="stREPL">
 * &gt; test-only *onebrowserpersuite*
 * [info] <span class="stGreen">FirefoxExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">- must provide an Application</span>
 * [info] <span class="stGreen">- must make the Application available implicitly</span>
 * [info] <span class="stGreen">- must start the Application</span>
 * [info] <span class="stGreen">- must provide the port number</span>
 * [info] <span class="stGreen">- must provide an actual running server</span>
 * [info] <span class="stGreen">- must provide a web driver</span>
 * [info] <span class="stGreen">SafariExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">- must provide an Application</span>
 * [info] <span class="stGreen">- must make the Application available implicitly</span>
 * [info] <span class="stGreen">- must start the Application</span>
 * [info] <span class="stGreen">- must provide the port number</span>
 * [info] <span class="stGreen">- must provide an actual running server</span>
 * [info] <span class="stGreen">- must provide a web driver</span>
 * [info] <span class="stGreen">InternetExplorerExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stYellow">- must provide an Application !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must make the Application available implicitly !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must start the Application !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must provide the port number !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must provide an actual running server !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must provide a web driver !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stGreen">ChromeExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stYellow">- must provide an Application !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must make the Application available implicitly !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must start the Application !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must provide the port number !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must provide an actual running server !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stYellow">- must provide a web driver !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (OneBrowserPerSuite.scala:201)</span>
 * [info] <span class="stGreen">HtmlUnitExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">- must provide an Application</span>
 * [info] <span class="stGreen">- must make the Application available implicitly</span>
 * [info] <span class="stGreen">- must start the Application</span>
 * [info] <span class="stGreen">- must provide the port number</span>
 * [info] <span class="stGreen">- must provide an actual running server</span>
 * [info] <span class="stGreen">- must provide a web driver</span>
 * </pre>
 * 
 * For comparison, here is what the output would look like if you just selected tests tagged with `FirefoxBrowser` in sbt:
 * 
 * <pre class="stREPL">
 * &gt; test-only *onebrowserpersuite* -- -n org.scalatest.tags.FirefoxBrowser
 * [info] <span class="stGreen">FirefoxExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">- must provide an Application</span>
 * [info] <span class="stGreen">- must make the Application available implicitly</span>
 * [info] <span class="stGreen">- must start the Application</span>
 * [info] <span class="stGreen">- must provide the port number</span>
 * [info] <span class="stGreen">- must provide an actual running server</span>
 * [info] <span class="stGreen">- must provide a web driver</span>
 * [info] <span class="stGreen">SafariExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">InternetExplorerExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">ChromeExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * [info] <span class="stGreen">HtmlUnitExampleSpec:</span>
 * [info] <span class="stGreen">The OneBrowserPerSuite trait</span>
 * </pre>
 */
trait OneBrowserPerSuite extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience with BrowserFactory { this: Suite with ServerProvider =>

  /**
   * An implicit instance of `WebDriver`, created by calling `createWebDriver`.  
   * If there is an error when creating the `WebDriver`, `UnavailableDriver` will be assigned 
   * instead.
   */
  implicit lazy val webDriver: WebDriver = createWebDriver()

  /**
   * Automatically cancels tests with an appropriate error message when the `webDriver` field is a `UnavailableDriver`,
   * else calls `super.withFixture(test)`
   */
  abstract override def withFixture(test: NoArgTest): Outcome = {
    webDriver match {
      case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
      case _ => super.withFixture(test)
    }
  }

  /**
   * Places the `WebDriver` provided by `webDriver` into the `ConfigMap` under the key
   * `org.scalatestplus.play.webDriver` to make
   * it available to nested suites; calls `super.run`; and lastly ensures the `WebDriver` is stopped after
   * all tests and nested suites have completed.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    val cleanup: Boolean => Unit = { _ =>
      webDriver match {
        case _: UnavailableDriver => // do nothing for UnavailableDriver
        case safariDriver: SafariDriver => safariDriver.quit()
        case chromeDriver: ChromeDriver => chromeDriver.quit()
        case _ => webDriver.close()
      }
    }
    try {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.webDriver" -> webDriver)
      val newArgs = args.copy(configMap = newConfigMap)
      val status = super.run(testName, newArgs)
      status.whenCompleted(cleanup)
      status
    } catch {
      case ex: Throwable =>
        cleanup(false)
        throw ex
    }
  }
}


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
import org.scalatest.events._
import org.scalatest.tags._
import selenium.WebBrowser
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import BrowserFactory.{NoDriver, WithoutDriver}
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Trait that uses a "shared test" approach to enable you to run the same tests on multiple browsers in a ScalaTest `Suite` with minimal boilerplate.
 *
 * This trait overrides `Suite`'s `withFixture` and `runTest` lifecycle methods to create a new `WebDriver`, `TestServer`, and
 * `FakeApplication` instance before executing each test, and overrides the `tags` lifecycle method to tag the shared tests so you can
 * filter them by browser type.
 *
 * You'll need to place any tests that you want executed by multiple browsers in a `registerSharedTests` method and, since all tests in a ScalaTest `Suite`
 * must have unique names, append the browser name (passed into `registerSharedTests`) to each test name:
 * 
 * <pre class="stHighlight">
 * def registerSharedTests(browser: BrowserInfo) {
 *   "The AllBrowsersPerSharedTest trait" must {
 *     "put the webDriver in the configMap" + browser.name in {
 *       val configuredWebDriver = configMap.getOptional[WebDriver]("org.scalatestplus.play.webDriver")
 *       configuredWebDriver mustBe defined
 *     } 
 * </pre>
 * 
 * All tests registered via `registerSharedTests` will be registered for each possible `WebDriver`. When running, any tests for browser drivers that are unavailable
 * on the current platform will be canceled. The tests/t
 * All tests registered under `registerSharedTests` will be
 * tagged automatically, when the test name ends with [Firefox] (returned from `browser.name` when `WebDriver` is `FirefoxDriver`),
 * the test will be automatically tagged with "org.scalatest.tags.FirefoxTag".  This means that you can include/exclude tests using ScalaTest's tagging feature.
 *
 */
trait AllBrowsersPerSharedTest extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite =>

  /**
   * Indicate whether to register tests for Firefox, by default returns `true`.  You can override it to return `false`
   * to always disable tests for Firefox.
   */
  lazy val registerSharedTestsForFirefox = true

  /**
   * Indicate whether to register tests for Safari, by default returns `true`.  You can override it to return `false`
   * to always disable tests for Safari.
   */
  lazy val registerSharedTestsForSafari = true

  /**
   * Indicate whether to register tests for Internet Explorer, by default returns `true`.  You can override it to return `false`
   * to always disable tests for Internet Explorer.
   */
  lazy val registerSharedTestsForInternetExplorer = true

  /**
   * Indicate whether to register tests for Chrome, by default returns `true`.  You can override it to return `false`
   * to always disable tests for Chrome.
   */
  lazy val registerSharedTestsForChrome = true

  /**
   * Indicate whether to register tests for HtmlUnit, by default returns `true`.  You can override it to return `false`
   * to always disable tests for HtmlUnit.
   */
  lazy val registerSharedTestsForHtmlUnit = true

  /**
   * Method to provide `FirefoxProfile` for creating `FirefoxDriver`, you can override this method to
   * provide a customized instance of `FirefoxProfile`
   *
   * @return an instance of `FirefoxProfile`
   */
  protected lazy val firefoxProfile: FirefoxProfile = new FirefoxProfile

  // Not sealed on purpose, so people can extend it if other
  // Browser driver types appear (or we could just use strings
  // for the browser names)
  /**
   * Abstract super class to represent a browser.
   *
   * @param name the browser name
   * @param tagName the browser tag name
   */
  abstract class BrowserInfo(val name: String, val tagName: String) {
    /**
     * Create `WebDriver` instance for the represented browser.
     *
     * @return `WebDriver` instance for the represented browser
     */
    def createWebDriver: WebDriver
  }

  /**
   * Case object for Firefox browser.
   */
  case object ForFirefox extends BrowserInfo(" [Firefox]", "org.scalatest.tags.FirefoxTag") {
    /**
     * Create `FirefoxDriver` instance.
     *
     * @return `FirefoxDriver` instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createFirefoxDriver(firefoxProfile)
  }

  /**
   * Case object for Safari browser.
   */
  case object ForSafari extends BrowserInfo(" [Safari]", "org.scalatest.tags.SafariTag") {
    /**
     * Create `SafariDriver` instance.
     *
     * @return `SafariDriver` instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createSafariDriver
  }

  /**
   * Case object for Internet Explorer browser.
   */
  case object ForInternetExplorer extends BrowserInfo(" [InternetExplorer]", "org.scalatest.tags.InternetExplorerTag") {
    /**
     * Create `InternetExplorerDriver` instance.
     *
     * @return `InternetExplorerDriver` instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createInternetExplorerDriver
  }

  /**
   * Case object for Chrome browser.
   */
  case object ForChrome extends BrowserInfo(" [Chrome]", "org.scalatest.tags.ChromeTag") {
    /**
     * Create `ChromeDriver` instance.
     *
     * @return `ChromeDriver` instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createChromeDriver
  }

  /**
   * Case object for HtmlUnit browser.
   */
  case object ForHtmlUnit extends BrowserInfo(" [HtmlUnit]", "org.scalatest.tags.HtmlUnitTag") {
    /**
     * Create `HtmlUnitDriver` instance.
     *
     * @return `HtmlUnitDriver` instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createHtmlUnitDriver
  }

  /**
   * Available browsers, you can override to add in your custom `BrowserInfo` implementation.
   */
  protected val browsers: IndexedSeq[BrowserInfo] =
    Vector(
      ForFirefox,
      ForSafari,
      ForInternetExplorer,
      ForChrome,
      ForHtmlUnit
    )

  private var privateApp: FakeApplication = _

  /**
   * Method to create new instance of `FakeApplication`
   */
  implicit def app: FakeApplication = synchronized { privateApp }

  /**
   * The port used by the `TestServer`.  By default this will be set to the result return from
   * `Helpers.testServerPort`, user can override this to provide their own port number.
   */
  val port: Int = Helpers.testServerPort

  private var privateWebDriver: WebDriver = _

  private var privateWebDriverName: String = _

  /**
   * Implicit method to get the `WebDriver` for the current test.
   */
  implicit def webDriver: WebDriver = synchronized { privateWebDriver }

  /**
   * Register shared tests.
   *
   * @param browser the passed in `BrowserInfo` instance, you must append `browser.name` to all tests register here.
   */
  def registerSharedTests(browser: BrowserInfo): Unit

  if (registerSharedTestsForFirefox) registerSharedTests(ForFirefox)
  if (registerSharedTestsForSafari) registerSharedTests(ForSafari)
  if (registerSharedTestsForInternetExplorer) registerSharedTests(ForInternetExplorer)
  if (registerSharedTestsForChrome) registerSharedTests(ForChrome)
  if (registerSharedTestsForHtmlUnit) registerSharedTests(ForHtmlUnit)

  private def mergeMap[A, B](ms: List[Map[A, B]])(f: (B, B) => B): Map[A, B] =
    (Map[A, B]() /: (for (m <- ms; kv <- m) yield kv)) { (a, kv) =>
      a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
    }

  /**
   * Overriden to automatically tag browser tests with browser tags.  Note that the browser tags will be merged with result returned from
   * `super.tags`.
   *
   * @return `super.tags` with additional browser tags automatically
   */
  abstract override def tags: Map[String, Set[String]] = {
    val generatedBrowserTags: Map[String, Set[String]] = Map.empty ++ testNames.map { tn =>
      browsers.find(b => tn.endsWith(b.name)) match {
        case Some(b) => (tn, Set(b.tagName))
        case None => (tn, Set.empty[String])
      }
    }
    mergeMap(List(super.tags, generatedBrowserTags.filter(!_._2.isEmpty))) { case (s1, s2) =>
      s1 ++ s2  // just add the 2 sets together
    }
  }

  /**
   * Override `withFixture` to check `WebDriver` before running each test.  If it is a
   * `NoDriver` all tests will be canceled automatically.  If valid `WebDriver` is available,
   * a new instance of `TestServer` will be started for each test before they are executed.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest): Outcome =
    webDriver match {
      case NoDriver(ex) =>
        val name = test.configMap.getRequired[String]("org.scalatestplus.play.webDriverName")
        val message = Resources("cantCreateDriver", name.trim)
        ex match {
          case Some(e) => Canceled(message, e)
          case None => Canceled(message)
        }
      case _ =>
        Helpers.running(TestServer(port, app)) {
          super.withFixture(test)
        }
    }

  /**
   * Override `runTest` to create `WebDriver` and `FakeApplication` before executing the test, and close the
   * `WebDriver` instance after the test is run.
   *
   * @param testName the name of one test to run.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when the test started by this method has completed, and whether or not it failed .
   */
  abstract override def runTest(testName: String, args: Args): Status = {
    // looks at the end of the test name, and if it is one of the blessed ones,
    // sets the port, driver, etc., before, and cleans up after, calling super.runTest
    synchronized {
      privateApp = new FakeApplication()
      val (theWebDriver, theWebDriverName) =
        browsers.find(b => testName.endsWith(b.name)) match {
          case Some(b) => (b.createWebDriver, b.name)
          case None => (WithoutDriver, "WithoutDriver")
        }
      privateWebDriver = theWebDriver
      privateWebDriverName = theWebDriverName
    }
    try {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app) + ("org.scalatestplus.play.port" -> port) + ("org.scalatestplus.play.webDriver" -> webDriver) + ("org.scalatestplus.play.webDriverName" -> privateWebDriverName)
      val newArgs = args.copy(configMap = newConfigMap)
      super.runTest(testName, newArgs)
    }
    finally {
      webDriver match {
        case NoDriver(_) => // do nothing
        case WithoutDriver => // do nothing
        case safariDriver: SafariDriver => safariDriver.quit()
        case chromeDriver: ChromeDriver => chromeDriver.quit()
        case theDriver => theDriver.close()
      }
    }
  }

}

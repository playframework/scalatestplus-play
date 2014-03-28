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
import BrowserDriver.{NoDriver, WithoutDriver}
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Trait that provides new browser instances (for all browsers available on the running platform) for each test executed in a ScalaTest <code>Suite</code>.
 *
 * It overrides ScalaTest's <code>withFixture</code> and <code>runTest</code> method to create new <code>WebDriver</code>, <code>TestServer</code> and
 * <code>FakeApplication</code> instance before executing each test.
 *
 * For tests that need to be shared between multiple browsers, you'll need to put them in <code>registerSharedTests</code> and append <code>forBrowser.name</code>
 * to the test name.  All tests registered under <code>registerSharedTests</code> will be provided with real <code>WebDriver</code> available on the system.
 * For unavailable <code>WebDriver</code> on the running platform, the test will be canceled.  All tests registered under <code>registerSharedTests</code> will be
 * tagged automatically, when the test name ends with [Firefox] (returned from <code>forBrowser.name</code> when <code>WebDriver</code> is <code>FirefoxDriver</code>),
 * the test will be automatically tagged with "org.scalatest.tags.Firefox".  This means that you can include/exclude tests using ScalaTest's tagging feature.
 *
 */
trait AllBrowsersPerTest extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite =>

  /**
   * Indicate whether to register tests for Firefox, by default returns <code>true</code>.  You can override it to return <code>false</code>
   * to always disable tests for Firefox.
   */
  lazy val registerSharedTestsForFirefox = true

  /**
   * Indicate whether to register tests for Safari, by default returns <code>true</code>.  You can override it to return <code>false</code>
   * to always disable tests for Safari.
   */
  lazy val registerSharedTestsForSafari = true

  /**
   * Indicate whether to register tests for Internet Explorer, by default returns <code>true</code>.  You can override it to return <code>false</code>
   * to always disable tests for Internet Explorer.
   */
  lazy val registerSharedTestsForInternetExplorer = true

  /**
   * Indicate whether to register tests for Chrome, by default returns <code>true</code>.  You can override it to return <code>false</code>
   * to always disable tests for Chrome.
   */
  lazy val registerSharedTestsForChrome = true

  /**
   * Indicate whether to register tests for HtmlUnit, by default returns <code>true</code>.  You can override it to return <code>false</code>
   * to always disable tests for HtmlUnit.
   */
  lazy val registerSharedTestsForHtmlUnit = true

  /**
   * Method to provide <code>FirefoxProfile</code> for creating <code>FirefoxDriver</code>, you can override this method to
   * provide a customized instance of <code>FirefoxProfile</code>
   *
   * @return an instance of <code>FirefoxProfile</code>
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
  abstract class ForBrowser(val name: String, val tagName: String) {
    /**
     * Create <code>WebDriver</code> instance for the represented browser.
     *
     * @return <code>WebDriver</code> instance for the represented browser
     */
    def createWebDriver: WebDriver
  }

  /**
   * Case object for Firefox browser.
   */
  case object ForFirefox extends ForBrowser(" [Firefox]", "org.scalatest.tags.Firefox") {
    /**
     * Create <code>FirefoxDriver</code> instance.
     *
     * @return <code>FirefoxDriver</code> instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createFirefoxDriver(firefoxProfile)
  }

  /**
   * Case object for Safari browser.
   */
  case object ForSafari extends ForBrowser(" [Safari]", "org.scalatest.tags.Safari") {
    /**
     * Create <code>SafariDriver</code> instance.
     *
     * @return <code>SafariDriver</code> instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createSafariDriver
  }

  /**
   * Case object for Internet Explorer browser.
   */
  case object ForInternetExplorer extends ForBrowser(" [InternetExplorer]", "org.scalatest.tags.InternetExplorer") {
    /**
     * Create <code>InternetExplorerDriver</code> instance.
     *
     * @return <code>InternetExplorerDriver</code> instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createInternetExplorerDriver
  }

  /**
   * Case object for Chrome browser.
   */
  case object ForChrome extends ForBrowser(" [Chrome]", "org.scalatest.tags.Chrome") {
    /**
     * Create <code>ChromeDriver</code> instance.
     *
     * @return <code>ChromeDriver</code> instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createChromeDriver
  }

  /**
   * Case object for HtmlUnit browser.
   */
  case object ForHtmlUnit extends ForBrowser(" [HtmlUnit]", "org.scalatest.tags.HtmlUnit") {
    /**
     * Create <code>HtmlUnitDriver</code> instance.
     *
     * @return <code>HtmlUnitDriver</code> instance
     */
    def createWebDriver: WebDriver = WebDriverFactory.createHtmlUnitDriver
  }

  /**
   * Available browsers, you can override to add in your custom <code>ForBrowser</code> implementation.
   */
  protected val browsers: IndexedSeq[ForBrowser] =
    Vector(
      ForFirefox,
      ForSafari,
      ForInternetExplorer,
      ForChrome,
      ForHtmlUnit
    )

  private var privateApp: FakeApplication = _

  /**
   * Method to create new instance of <code>FakeApplication</code>
   */
  implicit def app: FakeApplication = synchronized { privateApp }

  /**
   * The port used by the <code>TestServer</code>.  By default this will be set to the result return from
   * <code>Helpers.testServerPort</code>, user can override this to provide their own port number.
   */
  val port: Int = Helpers.testServerPort

  private var privateWebDriver: WebDriver = _

  private var privateWebDriverName: String = _

  /**
   * Implicit method to get the <code>WebDriver</code> for the current test.
   */
  implicit def webDriver: WebDriver = synchronized { privateWebDriver }

  /**
   * Register shared tests.
   *
   * @param forBrowser the passed in <code>ForBrowser</code> instance, you must append <code>forBrowser.name</code> to all tests register here.
   */
  def registerSharedTests(forBrowser: ForBrowser): Unit

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
   * <code>super.tags</code>.
   *
   * @return <code>super.tags</code> with additional browser tags automatically
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
   * Override <code>withFixture</code> to check <code>WebDriver</code> before running each test.  If it is a
   * <code>NoDriver</code> all tests will be canceled automatically.  If valid <code>WebDriver</code> is available,
   * a new instance of <code>TestServer</code> will be started for each test before they are executed.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the <code>Outcome</code> of the test execution
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
   * Override <code>runTest</code> to create <code>WebDriver</code> and <code>FakeApplication</code> before executing the test, and close the
   * <code>WebDriver</code> instance after the test is run.
   *
   * @param testName the name of one test to run.
   * @param args the <code>Args</code> for this run
   * @return a <code>Status</code> object that indicates when the test started by this method has completed, and whether or not it failed .
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

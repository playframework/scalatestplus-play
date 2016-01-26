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
import play.api.test._
import org.scalatest._
import org.scalatest.events._
import org.scalatest.tags._
import selenium.WebBrowser
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import BrowserFactory.GrumpyDriver
import BrowserFactory.UnavailableDriver
import BrowserFactory.UnneededDriver
import BrowserFactory.UninitializedDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Trait that uses a [[http://doc.scalatest.org/2.1.3/index.html#org.scalatest.FlatSpec@sharedTests ''shared test'']] approach to enable
 * you to run the same tests on multiple browsers in a ScalaTest `Suite`, where a new browser is started before each test
 * that needs a browser, and stopped after. 
 *
 * Note: the difference between this trait and [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]] is that
 * `AllBrowsersPerSuite` will allow you to write tests that rely on maintaining browser state between the tests. Thus, `AllBrowsersPerSuite` is a good fit
 * for integration tests in which each test builds on actions taken by the previous tests. This trait is good if your tests
 * each need a brand new browser.
 *
 * This trait overrides `Suite`'s `withFixture` lifecycle method to create a new `WebDriver`
 * instance before executing each test that needs a browser, closing it after the test completes, and overrides the `tags` lifecycle method to tag the shared tests so you can
 * filter them by browser type.  This trait's self-type, [[org.scalatestplus.play.ServerProvider ServerProvider]],  will ensure 
 * a `TestServer` and `Application` are available to each test. The self-type will require that you mix in either
 * [[org.scalatestplus.play.OneServerPerSuite OneServerPerSuite]], [[org.scalatestplus.play.OneServerPerTest OneServerPerTest]], 
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredServer]] before you mix in this trait. Your choice among these three
 * `ServerProvider`s will determine the extent to which a `TestServer` is shared by multiple tests.
 *
 * You'll need to place any tests that you want executed by multiple browsers in a `sharedTests` method. Because all tests in a ScalaTest `Suite`
 * must have unique names, you'll need to append the browser name (available from the `BrowserInfo` passed
 * to `sharedTests`) to each test name:
 * 
 * <pre class="stHighlight">
 * def sharedTests(browser: BrowserInfo) {
 *   "The blog app home page" must {
 *     "have the correct title " + browser.name in {
 *        go to (host + "index.html")
 *        pageTitle must be ("Awesome Blog")
 *     } 
 * </pre>
 * 
 * All tests registered via `sharedTests` will be registered for each desired `WebDriver`, as specified by the `browsers` field. When
 * running, any tests for browser drivers that are unavailable
 * on the current platform will be canceled.
 * All tests registered under `sharedTests` will be
 * tagged automatically if they end with a browser name in square brackets. For example, if a test name ends
 * with `[Firefox]`, it will be automatically tagged with `"org.scalatest.tags.FirefoxBrowser"`. This will
 * allow you can include or exclude the shared tests by browser type using ScalaTest's regular tagging feature.
 *
 * You can use tagging to include or exclude browsers that you sometimes want to test with, but not always. If you
 * ''never'' want to test with a particular browser, you can prevent tests for it from being registered at all
 * by overriding `browsers` and excluding its `BrowserInfo` in the returned `Seq`. For example, to disable registration of
 * tests for `HtmlUnit`, you'd write:
 *
 * <pre class="stHighlight">
 * override lazy val browsers: IndexedSeq[BrowserInfo] =
 *   Vector(
 *     FirefoxInfo,
 *     SafariInfo,
 *     InternetExplorerInfo,
 *     ChromeInfo
 *   )
 * </pre>
 *
 * Note that this trait can only be mixed into traits that register tests as functions, as the shared tests technique
 * is not possible in style traits that declare tests as methods, such as `org.scalatest.Spec`. Attempting to do so
 * will become a type error once we release ScalaTest 2.2.0.
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.allbrowserspertest
 *
 * import play.api.test._
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 * import play.api.routing._
 * import play.api.cache.EhCacheModule
 *
 * class ExampleSpec extends PlaySpec with OneServerPerTest with AllBrowsersPerTest {
 *
 *   // Override newAppForTest if you need a Application with other than non-default parameters.
 *   override def newAppForTest(testData: TestData): Application =
 *      new GuiceApplicationBuilder().disable[EhCacheModule].configure("foo" -> "bar").additionalRouter(Router.from(TestRoute)).build()
 *
 *   // Place tests you want run in different browsers in the `sharedTests` method:
 *   def sharedTests(browser: BrowserInfo) = {
 *
 *     "The AllBrowsersPerTest trait" must {
 *       "provide a web driver " + browser.name in {
 *         go to ("http://localhost:" + port + "/testing")
 *         pageTitle mustBe "Test Page"
 *         click on find(name("b")).value
 *         eventually { pageTitle mustBe "scalatest" }
 *       }
 *     }
 *   }
 *
 *   // Place tests you want run just once outside the `sharedTests` method
 *   // in the constructor, the usual place for tests in a `PlaySpec`
 *   "The AllBrowsersPerTest trait" must {
 *     "provide a FakeApplication" in {
 *       app.configuration.getString("foo") mustBe Some("bar")
 *     }
 *     "make the FakeApplication available implicitly" in {
 *        def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
 *       getConfig("foo") mustBe Some("bar")
 *     }
 *     "start the FakeApplication" in {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *     "provide the port" in {
 *       port mustBe Helpers.testServerPort
 *     }
 *     "provide an actual running server" in {
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
 * Here's how the output would look if you ran the above test class in sbt on a platform that
 * did not support Selenium drivers for Internet Explorer or Chrome:
 *
 * <pre class="stREPL">
 * &gt; test-only *allbrowserspersharedtest*
 * [info] <span class="stGreen">ExampleSpec:</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">- must provide a web driver [Firefox]</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">- must provide a web driver [Safari]</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stYellow">- must provide a web driver [InternetExplorer] !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium InternetExplorerDriver on this platform. (AllBrowsersPerTest.scala:257)</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stYellow">- must provide a web driver [Chrome] !!! CANCELED !!!</span>
 * [info]   <span class="stYellow">Was unable to create a Selenium ChromeDriver on this platform. (AllBrowsersPerTest.scala:257)</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">- must provide a web driver [HtmlUnit]</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">- must provide a Application</span>
 * [info] <span class="stGreen">- must make the Application available implicitly</span>
 * [info] <span class="stGreen">- must start the Application</span>
 * [info] <span class="stGreen">- must provide the port</span>
 * [info] <span class="stGreen">- must provide an actual running server</span>
 * </pre>
 *
 * Because the shared tests will be tagged according to browser, you can include or exclude tests based
 * on the browser they use. For example, here's how the output would look if you ran the above test class
 * with sbt and ask to include only Firefox:
 *
 * <pre class="stREPL">
 * &gt; test-only *allbrowserspersharedtest* -- -n org.scalatest.tags.FirefoxBrowser
 * [info] <span class="stGreen">ExampleSpec:</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">- must provide a web driver [Firefox]</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * [info] <span class="stGreen">The AllBrowsersPerTest trait</span>
 * </pre>
 */
trait AllBrowsersPerTest extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite with ServerProvider =>

  /**
   * Method to provide `FirefoxProfile` for creating `FirefoxDriver`, you can override this method to
   * provide a customized instance of `FirefoxProfile`
   *
   * @return an instance of `FirefoxProfile`
   */
  protected lazy val firefoxProfile: FirefoxProfile = new FirefoxProfile

  /**
   * Info for available browsers. Override to add in custom `BrowserInfo` implementations.
   */
  protected lazy val browsers: IndexedSeq[BrowserInfo] =
    Vector(
      FirefoxInfo(firefoxProfile),
      SafariInfo,
      InternetExplorerInfo,
      ChromeInfo,
      HtmlUnitInfo(true)
    )

  private var privateApp: Application = _

  private var privateWebDriver: WebDriver = UninitializedDriver

  /**
   * Implicit method to get the `WebDriver` for the current test.
   */
  implicit def webDriver: WebDriver = synchronized { privateWebDriver }

  /**
   * Registers tests "shared" by multiple browsers.
   *
   * Implement this method by placing tests you wish to run for multiple browsers. This method
   * will be called during the initialization of this trait once for each browser whose `BrowserInfo`
   * appears in the `IndexedSeq` referenced from the `browsers` field.
   *
   * Make sure you append `browser.name` to each test declared in `sharedTests`, to ensure they
   * all have unique names. Here's an example:
   *
   * <pre class="stHighlight">
   * def sharedTests(browser: BrowserInfo) {
   *   "The blog app home page" must {
   *     "have the correct title " + browser.name in {
   *        go to (host + "index.html")
   *        pageTitle must be ("Awesome Blog")
   *     } 
   * </pre>
   * 
   * If you don't append `browser.name` to each test name you'll likely be rewarded with
   * a `DuplicateTestNameException` when you attempt to run the suite.
   *
   * @param browser the passed in `BrowserInfo` instance
   */
  def sharedTests(browser: BrowserInfo): Unit

  for (browser <- browsers) {
    sharedTests(browser)
  }

  /**
   * Automatically tag browser tests with browser tags based on the test name: if a test ends in a browser
   * name in square brackets, it will be tagged as using that browser. For example, if a test name
   * ends in `[Firefox]`, it will be tagged with `org.scalatest.tags.FirefoxBrowser`. The browser tags will be merged with
   * tags returned from `super.tags`, so no existing tags will be lost when the browser tags are added.
   *
   * @return `super.tags` with additional browser tags added for any browser-specific tests 
   */
  abstract override def tags: Map[String, Set[String]] = {

    def mergeMap[A, B](ms: List[Map[A, B]])(f: (B, B) => B): Map[A, B] =
      (for (m <- ms; kv <- m) yield kv).foldLeft(Map.empty[A, B]) { (a, kv) =>
        a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
      }

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
   * Inspects the current test name and if it ends with the name of one of the `BrowserInfo`s 
   * mentioned in the `browsers` `IndexedSeq`, creates a new web driver by invoking `createWebDriver` on
   * that `BrowserInfo` and, unless it is an `UnavailableDriver`, installs it so it will be returned by
   * `webDriver` during the test. (If the driver is unavailable on the host platform, the `createWebDriver`
   * method will return `UnavailableDriver`, and this `withFixture` implementation will cancel the test
   * automatically.) If the current test name does not end in a browser name, this `withFixture` method
   * installs `BrowserInfo.UnneededDriver` as the driver to be returned by `webDriver` during the test.
   * If the test is not canceled because of an unavailable driver, this `withFixture` method invokes
   * `super.withFixture` and ensures that the `WebDriver` is closed after `super.withFixture` returns.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest): Outcome = {
    val localWebDriver: WebDriver =
      browsers.find(b => test.name.endsWith(b.name)) match {
        case Some(b) => b.createWebDriver()
        case None => UnneededDriver
      }
    localWebDriver match {
      case UnavailableDriver(ex, errorMessage) =>
        ex match {
          case Some(e) => Canceled(errorMessage, e)
          case None => Canceled(errorMessage)
        }
      case _ =>
        synchronized {
          privateWebDriver = localWebDriver
        }
        try super.withFixture(test)
        finally {
          localWebDriver match {
            case _: GrumpyDriver => // do nothing
            case safariDriver: SafariDriver => safariDriver.quit()
            case chromeDriver: ChromeDriver => chromeDriver.quit()
            case otherDriver => otherDriver.close()
          }
        }
    }
  }
}


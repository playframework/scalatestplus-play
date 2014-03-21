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
import selenium.WebBrowser
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxProfile
import BrowserDriver.NoDriver

/**
 * Trait that provides facility to discover available <code>WebBrowser</code> on the running platform and create instances to run tests per ScalaTest <code>Suite</code>.
 *
 * It overrides ScalaTest's <code>Suite.run</code> method to start a <code>TestServer</code> before test execution,
 * and stop the <code>TestServer</code> after test execution has completed.  Additionally, it will discover available <code>WebBrowser</code> on the running platform, and
 * call <code>super.run</code> for each available <code>WebBrowser</code> instance.  You can access the <code>FakeApplication</code>
 * in <code>args.configMap</code> using the <code>"app"</code> key, the port number of the <code>TestServer</code> using the <code>"port"</code> key,
 * the <code>WebDriver</code> instance using <code>"webDriver"</code> key and the name of the running <code>WebDriver</code> instance using <code>"webDriverName"</code>.
 * By default, this traits also overrides <code>Suite.withFixture</code> to cancel all the tests automatically if the related <code>WebDriver</code> is not available in the
 * running system.
 *
 * You can explicitly specify which <code>WebBrowser</code>(s) to run though config map's <code>"browsers"</code> key:
 *
 * <ul>
 *   <li>C - Chrome</li>
 *   <li>F - Firefox</li>
 *   <li>I - Internet Explorer</li>
 *   <li>S - Safari</li>
 *   <li>H - HtmlUnit</li>
 * </ul>
 *
 * For example, you can pass in <code>-Dbrowsers="CF"</code> to run <code>Chrome</code> and <code>Firefox</code> only.
 *
 * If no valid web browser is specified through config map's <code>"browsers"</code> string, it will fallback to default which discover all available <code>WebDriver</code>
 * and run with them.
 *
 */
trait AllBrowsersPerSuite extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite =>

  /**
   * An implicit instance of <code>FakeApplication</code>.
   */
  implicit val app: FakeApplication = new FakeApplication()

  /**
   * The port used by the <code>TestServer</code>.  By default this will be set to the result return from
   * <code>Helpers.testServerPort</code>, user can override this to provide their own port number.
   */
  val port: Int = Helpers.testServerPort

  private var privateWebDriver: WebDriver = _

  /**
   * Implicit method to get the <code>WebDriver</code> for the current test.
   */
  implicit def webDriver: WebDriver = synchronized { privateWebDriver }

  /**
   * Method to provide <code>FirefoxProfile</code> for creating <code>FirefoxDriver</code>, you can override this method to
   * provide a customized instance of <code>FirefoxProfile</code>
   *
   * @return an instance of <code>FirefoxProfile</code>
   */
  protected def firefoxProfile: FirefoxProfile = new FirefoxProfile

  /**
   * Override to cancel tests automatically when <code>webDriver</code> resolve to <code>NoDriver</code>
   */
  abstract override def withFixture(test: NoArgTest): Outcome = {
    webDriver match {
      case NoDriver(ex) =>
        val name = test.configMap("webDriverName")
        val message = Resources("cantCreateDriver", name)
        ex match {
          case Some(e) => cancel(message, e)
          case None => cancel(message)
        }
      case _ => super.withFixture(test)
    }
  }

  /**
   * Overriden to start <code>TestServer</code> before running the tests, pass a <code>FakeApplication</code> into the tests in
   * <code>args.configMap</code> via "app" key, <code>TestServer</code>'s port number via "port", <code>WebDriver</code>
   * instance via "webDriver" key and the name of <code>WebDriver</code> via "webDriverName" key.  It then discover available
   * <code>WebBrowser</code> on the running platform (and filter them if -Dbrowsers=... is specified through config map) and calls
   * <code>super.run</code> for each of them to execute the tests.  An instance of <code>WebBrowser</code> will be closed first before
   * the next one is created and run.  Upon completion, it stops the <code>TestServer</code>.
   *
   * @param testName an optional name of one test to run. If <code>None</code>, all relevant tests should be run.
   *                 I.e., <code>None</code> acts like a wildcard that means run all relevant tests in this <code>Suite</code>.
   * @param args the <code>Args</code> for this run
   * @return a <code>Status</code> object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    val testServer = TestServer(port, app)
    val availableWebDrivers =
      Set(
        ("Chrome", () => WebDriverFactory.createChromeDriver),
        ("Firefox", () => WebDriverFactory.createFirefoxDriver(firefoxProfile)),
        ("Internet Explorer", () => WebDriverFactory.createInternetExplorerDriver),
        ("Safari", () => WebDriverFactory.createSafariDriver),
        ("HtmlUnit", () => WebDriverFactory.createHtmlUnitDriver)
      )

    val filterWebDrivers =
      args.configMap.getOptional[String]("browsers") match {
        case Some("") =>
          args.reporter(AlertProvided(
            args.tracker.nextOrdinal(),
            Resources("emptyBrowsers"),
            Some(NameInfo(this.suiteName, this.suiteId, Some(this.getClass.getName), testName))
          ))
          availableWebDrivers

        case Some(browsers) =>
          val invalidChars = browsers.filter(c => !"CFISH".contains(c.toString.toUpperCase))
          if (!invalidChars.isEmpty) {
            val (resourceName, charsString) =
              if (invalidChars.length > 1) {
                val initString = invalidChars.init.map(c => "'" + c + "'").mkString(Resources("commaSpace"))
                ("invalidBrowsersChars", Resources("and", initString, "'" + invalidChars.last  + "'"))
              }
              else
                ("invalidBrowsersChar", "'" + invalidChars.head + "'")
            args.reporter(AlertProvided(
              args.tracker.nextOrdinal(),
              Resources(resourceName, charsString),
              Some(NameInfo(this.suiteName, this.suiteId, Some(this.getClass.getName), testName))
            ))
          }
          val filteredDrivers =
            availableWebDrivers.filter { case (name, webDriverFun) =>
              browsers.toUpperCase.contains(name.charAt(0))
            }

          // If no valid option, just fallback to default that uses all available browsers
          if (filteredDrivers.isEmpty)
            availableWebDrivers
          else
            filteredDrivers

        case None => availableWebDrivers
      }

    try {
      testServer.start()
      new CompositeStatus(
        (filterWebDrivers.map { case (name, driverFun) =>
          synchronized {
            privateWebDriver = driverFun()
          }
          val newConfigMap = args.configMap + ("app" -> app) + ("port" -> port) + ("webDriver" -> webDriver) + ("webDriverName" -> name)
          val newArgs = args.copy(configMap = newConfigMap)
          try {
            super.run(testName, newArgs)
          }
          finally {
            privateWebDriver match {
              case NoDriver(_) => // do nothing for NoDriver
              case theDriver => theDriver.close()
            }
          }
        }).toSet
      )
    } finally {
      testServer.stop()
    }
  }
}


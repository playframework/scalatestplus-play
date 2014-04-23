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
 * Trait that helps you provide different fixtures to each test: a `FakeApplication`, a `TestServer`, or one
 * of the Selenium `WebBrowser`s.
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


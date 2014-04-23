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
 * Trait that helps you provide different fixtures to each test: a <code>FakeApplication</code>, a <code>TestServer</code>, or one
 * of the Selenium <code>WebBrowser</code>s.
 */
trait MixedFixtures extends SuiteMixin with UnitFixture { this: fixture.Suite =>

  /**
   * Class that provides fixture for <code>FakeApplication</code>, it will run the passed in <code>FakeApplication</code> 
   * before running the test.
   */
  abstract class App(val app: FakeApplication = FakeApplication()) extends NoArg {
    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Override to run the passed in <code>FakeApplication</code> first before running the test.
     */
    override def apply() {
      Helpers.running(app)(super.apply())
    }
  }

  /**
   * Class that provides fixture for <code>TestServer</code> and <code>FakeApplication</code>, 
   * it will run the passed in <code>TestServer</code> before running the test.
   */
  abstract class Server(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends NoArg {
    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
     * will be same as the value of <code>port</code>.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a <code>TestServer</code> using the passed in <code>port</code> before running the test.
     */
    override def apply() {
      Helpers.running(TestServer(port, app))(super.apply())
    }
  }

  /**
   * Class that provides fixture for <code>HtmlUnit</code> browser.
   */
  abstract class HtmlUnit(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with HtmlUnitFactory {
    /**
     * A lazy implicit instance of <code>HtmlUnitDriver</code>, it will hold <code>NoDriver</code> if <code>HtmlUnitDriver</code> 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
     * will be same as the value of <code>port</code>.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a <code>TestServer</code> using the passed in <code>port</code> before running the test, 
     * and close the <code>HtmlUnitDriver</code> automatically after test execution.
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
   * Class that provides fixture for <code>Firefox</code> browser.
   */
  abstract class Firefox(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with FirefoxFactory {

    /**
     * A lazy implicit instance of <code>FirefoxDriver</code>, it will hold <code>NoDriver</code> if <code>FirefoxDriver</code> 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
     * will be same as the value of <code>port</code>.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a <code>TestServer</code> using the passed in <code>port</code> before running the test, 
     * and close the <code>FirefoxDriver</code> automatically after test execution.
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
   * Class that provides fixture for <code>Safari</code> browser.
   */
  abstract class Safari(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with SafariFactory {
    /**
     * A lazy implicit instance of <code>SafariDriver</code>, it will hold <code>NoDriver</code> if <code>SafariDriver</code> 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
     * will be same as the value of <code>port</code>.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a <code>TestServer</code> using the passed in <code>port</code> before running the test, 
     * and close the <code>SafariDriver</code> automatically after test execution.
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
   * Class that provides fixture for <code>Chrome</code> browser.
   */
  abstract class Chrome(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with ChromeFactory {
    /**
     * A lazy implicit instance of <code>ChromeDriver</code>, it will hold <code>NoDriver</code> if <code>ChromeDriver</code> 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
     * will be same as the value of <code>port</code>.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a <code>TestServer</code> using the passed in <code>port</code> before running the test, 
     * and close the <code>ChromeDriver</code> automatically after test execution.
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
   * Class that provides fixture for <code>InternetExplorer</code> browser.
   */
  abstract class InternetExplorer(val app: FakeApplication = FakeApplication(), val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with InternetExplorerFactory {
    /**
     * A lazy implicit instance of <code>InternetExplorerDriver</code>, it will hold <code>NoDriver</code> if <code>InternetExplorerDriver</code> 
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Make the passed in <code>FakeApplication</code> implicit.
     */
    implicit def implicitApp: FakeApplication = app

    /**
     * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
     * will be same as the value of <code>port</code>.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Override to run a <code>TestServer</code> using the passed in <code>port</code> before running the test, 
     * and close the <code>InternetExplorerDriver</code> automatically after test execution.
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


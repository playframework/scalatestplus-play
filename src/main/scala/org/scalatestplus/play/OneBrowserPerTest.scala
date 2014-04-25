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
import BrowserFactory.NoDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Trait that provides a new `FakeApplication`, running `TestServer`, and Selenium `WebDriver` instance for each test executed in a ScalaTest `Suite`.
 * 
 * This trait overrides ScalaTest's `withFixture` method to create a new `FakeApplication`, `TestServer`, and `WebDriver` instance 
 * before each test, and ensure they are cleaned up after the test has completed. The `FakeApplication` is available (implicitly) from
 * method `app`. The `TestServer`'s port number is available as `port` (and implicitly available as `portNumber`, wrapped in a [[org.scalatestplus.play.PortNumber PortNumber]]).
 * The `WebDriver` is available (implicitly) from method `webDriver`.
 */
trait OneBrowserPerTest extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience with BrowserFactory { this: Suite =>

  private var privateApp: FakeApplication = _

  /**
   * Implicit method that returns the `FakeApplication` instance for the current test.
   */
  implicit final def app: FakeApplication = synchronized { privateApp }

  /**
   * Creates new instance of `FakeApplication` with parameters set to their defaults. Override this method if you
   * need a `FakeApplication` created with non-default parameter values.
   */
  def newApp: FakeApplication = new FakeApplication()

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  lazy val port: Int = Helpers.testServerPort

  /**
   * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
   * will be same as the value of `port`.
   */
  implicit final lazy val portNumber: PortNumber = PortNumber(port)

  private var privateWebDriver: WebDriver = _

  /**
   * Implicit method to get the `WebDriver` for the current test.
   */
  implicit def webDriver: WebDriver = synchronized { privateWebDriver }

  /**
   * Override `withFixture` to create new instance of `WebDriver` before 
   * running each test.  If there is error when creating `WebDriver`, `NoDriver` 
   * will be used and all tests will be canceled automatically.  If `WebDirver` creation 
   * is successful, a new instance of `TestServer` will be started for each test before they 
   * are executed.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized {
      privateApp = newApp
      privateWebDriver = createWebDriver()
    }
    try {
      privateWebDriver match {
        case NoDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          Helpers.running(TestServer(port, app)) {
            super.withFixture(test)
          }
      }
    }
    finally {
      privateWebDriver match {
        case _: NoDriver => // do nothing
        case safariDriver: SafariDriver => safariDriver.quit()
        case chromeDriver: ChromeDriver => chromeDriver.quit()
        case _ => privateWebDriver.close()
      }
    }
  }
}


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
 * Trait that provides new browser instance for each test executed in a ScalaTest `Suite`.
 * 
 * It overrides ScalaTest's `withFixture` method to create new `WebDriver` instance 
 * before executing each test.
 */
trait OneBrowserPerTest extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience with BrowserFactory { this: Suite =>

  private var privateApp: FakeApplication = _

  /**
   * Implicit method that returns the `FakeApplication` instance for the current test.
   */
  implicit def app: FakeApplication = synchronized { privateApp }

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  val port: Int = Helpers.testServerPort

  /**
   * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
   * will be same as the value of `port`.
   */
  implicit lazy val portNumber: PortNumber = PortNumber(port)

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
      privateApp = new FakeApplication()
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

/*
Could I have OneBrowserPerTest that has a def webDriver?
Think so. Just like implicit def webDriver and privateWebDriver,
same kind of thing. So each test gets its own. Then what we need
is a factory that produces the web driver. And that's how they pick.
It would be nice if that was called Firefox, Chrome, etc. What I'd
kind of like is that ... yes, interestingly, seems like I wonder
if I could use the same for all 3. 

private lazy val sharedWebDriver = new Firefox
implicit def webDriver: WebDriver = sharedWebDriver

By default this just returns the val, but you can override it
in a subclass so that it returns a different one for each 
test. Then you can mix Firefox into any class right, so long as it
is to the left? OneFirefoxPerTest
OneChromePerTest. OneChromePerSuite is just Chrome.

And the mixed fixture one is jut named new Firefox {

Could I do OneBrowserPerTest and OneBrowserPerSuite and
have it fill in the browser type via a mixin?

OneBrowserPerTest with FirefoxBrowser

OK. Then how do we do the all browsers thing?

OneBrowserPerTest with AllBrowsers would be nice
withFixture needs to run the same tests for each browser that is available.
*/


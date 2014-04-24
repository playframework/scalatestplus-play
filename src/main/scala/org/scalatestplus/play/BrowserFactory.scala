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
import org.openqa.selenium.WebDriver

/**
 * Trait that defines an abstract `createWebDriver`  method for creating a new Selenium `WebDriver`
 * and an abstract `unableToCreateDriverErrorMessage` method that provides an appropriate error message if the driver
 * is not available on the current platform.
 */
trait BrowserFactory {

  /**
   * Creates a new instance of a Selenium `WebDriver`, or returns a `BrowserFactory.NoDriver` that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `WebDriver`, or a `BrowserFactory.NoDriver` if the desired
   * `WebDriver` is not available on the host platform.
   */
  def createWebDriver(): WebDriver
}

import org.openqa.selenium._

/**
 * Companion object to trait `BrowserFactory` that holds a `NoDriver` object that implements 
 * the Selenium `WebDriver` interface by throwing `UnuspportedOperationException`. This is
 * used as a placeholder when a driver is not available on the host platform.
 */
object BrowserFactory {

  /**
   * An implementation of `WebDriver` that provides an optional exception and an error message and throws `UnsupportedOperationException` from
   * all of its other methods, used when a requested Selenium `WebDriver` is unavailable on the host platform.
   *
   * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]], [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]],
   * [[org.scalatestplus.play.AllBrowsersPerSharedTest AllBrowsersPerSharedTest]], and [[org.scalatestplus.play.MixedFixtures MixedFixtures]] check
   * if the requested `WebDriver` is available, and if not, installs this driver (to avoid initializing with `null`)
   * and cancels the tests.
   *
   * This is an example of the "Null Object Pattern." We use this pattern to avoid initializing with `null` instead of making the driver type
   * an `Option[WebDriver]` for two reasons: 1) the type of the implicit needed by Selenium is `WebDriver`, not `Option[WebDriver]`, and 2) 
   * the Null Object we provide also carries an optional exception and user-friendly error message.
   *
   * @param ex: the `Throwable`, if any, that was thrown when attempting to use the requested driver
   * @param errorMessage: a user-friendly error message that mentions the specific driver that was unavailable on the host platform
   */
  case class NoDriver(ex: Option[Throwable], errorMessage: String) extends WebDriver {

    /**
     * Throws `UnsupportedOperationException`.
     */
    def close() { // TODO: Change the error messages to errorMessage
      throw new UnsupportedOperationException("close not supported")
    }
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def findElement(by: By): WebElement = 
      throw new UnsupportedOperationException("findElement not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def findElements(by: By): java.util.List[WebElement] = 
      throw new UnsupportedOperationException("findElements not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def get(url: String) {
      throw new UnsupportedOperationException("get not supported")
    }
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def getCurrentUrl(): String = 
      throw new UnsupportedOperationException("getCurrentUrl not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def getPageSource(): String = 
      throw new UnsupportedOperationException("getCurrentUrl not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def getTitle(): String = 
      throw new UnsupportedOperationException("getCurrentUrl not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def getWindowHandle(): String = 
      throw new UnsupportedOperationException("getWindowHandle not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def getWindowHandles(): java.util.Set[java.lang.String] = 
      throw new UnsupportedOperationException("getWindowHandles not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def manage(): WebDriver.Options = 
      throw new UnsupportedOperationException("manage not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def navigate(): WebDriver.Navigation = 
      throw new UnsupportedOperationException("navigate not supported")
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def quit() {
      throw new UnsupportedOperationException("quit not supported")
    }
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    def switchTo(): WebDriver.TargetLocator = 
      throw new UnsupportedOperationException("switchTo not supported")
  }
}

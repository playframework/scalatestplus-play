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
 * Trait that defines an abstract <code>createNewDriver</code>  method for creating a new Selenium <code>WebDriver</code>.
 */
trait BrowserDriver {

  /**
   * Create an new instance of Selenium <code>WebDriver</code>.
   *
   * @return an new instance of Selenium <code>WebDriver</code>
   */
  def createNewDriver: WebDriver

  /**
   * Error message to use if <code>createNewDriver</code> completes abruptly with an exception.
   */
  def cantCreateRequestedDriver: String
}

import org.openqa.selenium._

/**
 * Companion object to trait <code>BrowserDriver</code> that holds a <code>NoDriver</code> object that implements 
 * the Selenium <code>WebDriver</code> interface by throwing <code>UnuspportedOperationException</code>. This is
 * used as a placeholder when canceling tests because a web driver does not work on the host platform (such
 * as the driver for Internet Explorer on a Mac).
 */
object BrowserDriver {

  /**
   * An implementation of <code>BrowserDriver</code> that does nothing, used when a requested Selenium <code>WebDriver</code> is unavailable.
   * Traits <code>OneBrowserPerSuite</code>, <code>OneBrowserPerTest</code>, and <code>MixedFixtures</code> check
   * if the requested <code>WebDriver</code> is available, and if not installs this driver (to avoid initializing with <code>null</code>),
   * and cancels the tests.
   *
   * @param ex: the <code>Throwable</code>, if any, that was thrown when attempting to use the requested driver
   */
  case class NoDriver(ex: Option[Throwable]) extends WebDriver {
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def close() {
      throw new UnsupportedOperationException("close not supported")
    }
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def findElement(by: By): WebElement = 
      throw new UnsupportedOperationException("findElement not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def findElements(by: By): java.util.List[WebElement] = 
      throw new UnsupportedOperationException("findElements not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def get(url: String) {
      throw new UnsupportedOperationException("get not supported")
    }
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getCurrentUrl(): String = 
      throw new UnsupportedOperationException("getCurrentUrl not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getPageSource(): String = 
      throw new UnsupportedOperationException("getCurrentUrl not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getTitle(): String = 
      throw new UnsupportedOperationException("getCurrentUrl not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getWindowHandle(): String = 
      throw new UnsupportedOperationException("getWindowHandle not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getWindowHandles(): java.util.Set[java.lang.String] = 
      throw new UnsupportedOperationException("getWindowHandles not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def manage(): WebDriver.Options = 
      throw new UnsupportedOperationException("manage not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def navigate(): WebDriver.Navigation = 
      throw new UnsupportedOperationException("navigate not supported")
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def quit() {
      throw new UnsupportedOperationException("quit not supported")
    }
  
    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def switchTo(): WebDriver.TargetLocator = 
      throw new UnsupportedOperationException("switchTo not supported")
  }

  /**
   * An implementation of <code>BrowserDriver</code> that does nothing, used when a test in <code>AllBrowsersPerTest</code>
   * does not require a <code>WebDriver</code>.
   *
   */
  object WithoutDriver extends WebDriver {

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def close() {
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")
    }

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def findElement(by: By): WebElement =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def findElements(by: By): java.util.List[WebElement] =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def get(url: String) {
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")
    }

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getCurrentUrl(): String =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getPageSource(): String =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getTitle(): String =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getWindowHandle(): String =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def getWindowHandles(): java.util.Set[java.lang.String] =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def manage(): WebDriver.Options =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def navigate(): WebDriver.Navigation =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def quit() {
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")
    }

    /**
     * Throws <code>UnsupportedOperationException</code>.
     */
    def switchTo(): WebDriver.TargetLocator =
      throw new UnsupportedOperationException("This test is expected to work without any WebDriver, did you forget to register this test under registerSharedTests?")
  }
}

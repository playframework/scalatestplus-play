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
import org.openqa.selenium._

/**
 * An implementation of <code>BrowserDriver</code> that does nothing, used when a requested Selenium <code>WebDriver</code> is unavailable.
 * Traits <code>OneBrowserPerSuite</code>, <code>OneBrowserPerTest</code>, and <code>MixedFixtures</code> check
 * if the requested <code>WebDriver</code> is available, and if not installs this driver (to avoid initializing with <code>null</code>),
 * and cancels the tests.
 */
object NoDriver extends WebDriver {

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

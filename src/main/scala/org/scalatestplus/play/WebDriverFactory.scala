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

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatestplus.play.BrowserDriver.NoDriver

/**
 * Factory to create different types of <code>WebDriver</code>
 */
object WebDriverFactory {

  /**
   * Create Chrome web driver.
   *
   * @return An instance of <code>ChromeDriver</code>, <code>NoDriver</code> if Chrome is not available on the platform.
   */
  def createChromeDriver: WebDriver =
    try { new ChromeDriver() } catch { case ex: Throwable => NoDriver(Some(ex)) }

  /**
   * Create Firefox web driver.
   *
   * @param profile Firefox profile for creating Firefox web driver
   * @return An instance of <code>FirefoxDriver</code>, <code>NoDriver</code> if Firefox is not available on the platform.
   */
  def createFirefoxDriver(profile: FirefoxProfile = new FirefoxProfile): WebDriver =
    try { new FirefoxDriver(profile) } catch { case ex: Throwable => NoDriver(Some(ex)) }

  /**
   * Create Internet Explorer web driver.
   *
   * @return An instance of <code>InternetExplorerDriver</code>, <code>NoDriver</code> if Internet Explorer is not available on the platform.
   */
  def createInternetExplorerDriver: WebDriver =
    try { new InternetExplorerDriver } catch { case ex: Throwable => NoDriver(Some(ex)) }

  /**
   * Create Safari web driver.
   *
   * @return An instance of <code>SafariDriver</code>, <code>NoDriver</code> if Safari is not available on the platform.
   */
  def createSafariDriver: WebDriver =
    try { new SafariDriver } catch { case ex: Throwable => NoDriver(Some(ex)) }

  /**
   * Create Html Unit web driver.
   *
   * @return An instance of <code>HtmlUnitDriver</code>, <code>NoDriver</code> if Safari is not available on the platform.
   */
  def createHtmlUnitDriver: WebDriver =
    try {
      val htmlUnitDriver = new HtmlUnitDriver()
      htmlUnitDriver.setJavascriptEnabled(true)
      htmlUnitDriver
    }
    catch {
      case ex: Throwable => NoDriver(Some(ex))
    }
}

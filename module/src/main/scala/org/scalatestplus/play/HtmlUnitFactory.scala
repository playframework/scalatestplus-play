/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalatestplus.play

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatestplus.play.BrowserFactory.UnavailableDriver

/**
 * Factory whose `createWebDriver` method will either return a new Selenium `HtmlUnitDriver`, or
 * [[org.scalatestplus.play.BrowserFactory.UnavailableDriver UnavailableDriver]], if HtmlUnit is not available on the host platform.
 *
 * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]] and
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]] extend `BrowserFactory` and therefore require
 * you to fill in the `createWebDriver` method, usually by mixing in one of the `BrowserFactory` subtraits such as
 * `HtmlUnitFactory`.
 */
trait HtmlUnitFactory extends BrowserFactory {

  /**
   * Creates a new instance of a Selenium `HtmlUnitDriver`, with Javascript enabled, or returns a
   * [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes the exception that indicated the driver was not
   * supported on the host platform and an appropriate error message.
   *
   * @return an new instance of a Selenium `HtmlUnitDriver`, or a `BrowserFactory.UnavailableDriver` if an HtmlUnit driver is not
   * available on the host platform.
   */
  def createWebDriver(): WebDriver = HtmlUnitFactory.createWebDriver(true)
}

/**
 * Companion object to trait `HtmlUnitFactory` that mixes in the trait.
 */
object HtmlUnitFactory extends HtmlUnitFactory {

  /**
   * Creates a new instance of a Selenium `HtmlUnitDriver`, with Javascript enabled or disabled depending upon
   * the passed flag, or returns a `BrowserFactory.UnavailableDriver` that includes the exception that indicated the
   * driver was not supported on the host platform and an appropriate error message.
   *
   * @return an new instance of a Selenium `HtmlUnitDriver`, or a `BrowserFactory.UnavailableDriver` if an HtmlUnit driver is not
   * available on the host platform.
   */
  def createWebDriver(enableJavascript: Boolean): WebDriver =
    try {
      val htmlUnitDriver = new HtmlUnitDriver()
      htmlUnitDriver.setJavascriptEnabled(enableJavascript)
      htmlUnitDriver
    } catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreateHtmlUnitDriver", ex.getMessage))
    }
}

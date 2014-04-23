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
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import BrowserFactory.NoDriver

/**
 * Trait providing a <code>createWebDriver</code> method that creates a new Selenium <code>HtmlUnitDriver</code>.
 */
trait HtmlUnitFactory extends BrowserFactory {

  /**
   * Creates a new instance of a Selenium `HtmlUnitDriver`, with Javascript enabled, or returns a
   * `BrowserFactory.NoDriver` that includes the exception that indicated the driver was not
   * supported on the host platform and an appropriate error message.
   *
   * @return an new instance of a Selenium `HtmlUnitDriver`, or a `BrowserFactory.NoDriver` if an HtmlUnit driver is not 
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
   * the passed flag, or returns a `BrowserFactory.NoDriver` that includes the exception that indicated the
   * driver was not supported on the host platform and an appropriate error message.
   *
   * @return an new instance of a Selenium `HtmlUnitDriver`, or a `BrowserFactory.NoDriver` if an HtmlUnit driver is not 
   * available on the host platform.
   */
  def createWebDriver(enableJavascript: Boolean): WebDriver =
    try {
      val htmlUnitDriver = new HtmlUnitDriver()
      htmlUnitDriver.setJavascriptEnabled(enableJavascript)
      htmlUnitDriver
    }
    catch {
      case ex: Throwable => NoDriver(Some(ex), Resources("cantCreateHtmlUnitDriver"))
    }
}

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
import org.scalatest.events._
import org.scalatest.tags._
import selenium.WebBrowser
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

// Not sealed on purpose, so people can extend it if other
// Browser driver types appear (or we could just use strings
// for the browser names)
/**
 * Abstract super class for browser information used to register tests shared by multiple browser drivers.
 *
 * @param name the browser name, surrounded by square brackets
 * @param tagName the browser tag name
 */
abstract class BrowserInfo(val name: String, val tagName: String) {
  /**
   * Creates a `WebDriver` instance for the represented browser.
   *
   * @return `WebDriver` instance for the represented browser
   */
  def createWebDriver(): WebDriver
}

/**
 * Case object for Firefox browser info.
 */
case class FirefoxInfo(firefoxProfile: FirefoxProfile) extends BrowserInfo("[Firefox]", "org.scalatest.tags.FirefoxBrowser") {
  /**
   * Creates a `WebDriver` instance for Firefox.
   *
   * @return a Firefox `WebDriver` instance
   */
  def createWebDriver(): WebDriver = FirefoxFactory.createWebDriver(firefoxProfile)
}

/**
 * Case object for Safari browser info.
 */
case object SafariInfo extends BrowserInfo("[Safari]", "org.scalatest.tags.SafariBrowser") {
  /**
   * Creates a `WebDriver` instance for Safari.
   *
   * @return a Safari `WebDriver` instance
   */
  def createWebDriver(): WebDriver = SafariFactory.createWebDriver()
}

/**
 * Case object for Internet Explorer browser info.
 */
case object InternetExplorerInfo extends BrowserInfo("[InternetExplorer]", "org.scalatest.tags.InternetExplorerBrowser") {
  /**
   * Creates a `WebDriver` instance for Internet Explorer.
   *
   * @return an Internet Explorer `WebDriver` instance
   */
  def createWebDriver(): WebDriver = InternetExplorerFactory.createWebDriver()
}

/**
 * Case object for Chrome browser info.
 */
case object ChromeInfo extends BrowserInfo("[Chrome]", "org.scalatest.tags.ChromeBrowser") {
  /**
   * Creates a `WebDriver` instance for Chrome .
   *
   * @return a Chrome `WebDriver` instance
   */
  def createWebDriver(): WebDriver = ChromeFactory.createWebDriver()
}

/**
 * Case object for `HtmlUnit` browser info.
 */
case class HtmlUnitInfo(enableJavascript: Boolean) extends BrowserInfo("[HtmlUnit]", "org.scalatest.tags.HtmlUnitBrowser") {
  /**
   * Creates an `HtmlUnit` `WebDriver` instance.
   *
   * @return an `HtmlUnit` `WebDriver` instance
   */
  def createWebDriver(): WebDriver = HtmlUnitFactory.createWebDriver(enableJavascript)
}


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
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import BrowserFactory.UnavailableDriver

/**
 * Trait providing a 'createWebDriver' method that creates a new Selenium 'FirefoxDriver'.
 */
trait FirefoxFactory extends BrowserFactory {

  /**
   * 'FirefoxProfile' that is used to create new instance of 'FirefoxDriver'.
   * Override to provide a different `FirefoxProfile`.
   */
  lazy val firefoxProfile = new FirefoxProfile()

  /**
   * Creates a new instance of a Selenium `FirefoxDriver` (using the `FirefoxProfile` provided by
   * the `firefoxProfile` field), or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `FirefoxDriver` or a `BrowserFactory.UnavailableDriver` if a Firefox
   * driver is not available on the host platform.
   */
  def createWebDriver(): WebDriver =
    try {
      new FirefoxDriver(firefoxProfile)
    }
    catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreateFirefoxDriver"))
    }
}

/**
 * Companion object to trait `FirefoxFactory` that mixes in the trait.
 */
object FirefoxFactory extends FirefoxFactory {

  // This factory method is used by FirefoxInfo in AllBrowsersPerSharedTest.

  /**
   * Creates a new instance of a Selenium `FirefoxDriver`, using the specified `FirefoxProfile`,
   * or returns a `BrowserFactory.UnavailableDriver` that includes the exception that indicated the driver
   * was not supported on the host platform and an appropriate error message.
   *
   * @return a new instance of a Selenium `FirefoxDriver`, using the specified `FirefoxProfile`,
   *   or a `BrowserFactory.UnavailableDriver` if a Firefox driver is not available on the host platform.
   */
  def createWebDriver(firefoxProfile: FirefoxProfile): WebDriver =
    try {
      new FirefoxDriver(firefoxProfile)
    }
    catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreateFirefoxDriver"))
    }
}

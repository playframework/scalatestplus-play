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
import org.openqa.selenium.firefox.*
import org.scalatestplus.play.BrowserFactory.UnavailableDriver

/**
 * Factory whose `createWebDriver` method will either return a new Selenium `FirefoxDriver` (created
 * using the profile specified by `firefoxProfile`), or
 * [[org.scalatestplus.play.BrowserFactory.UnavailableDriver UnavailableDriver]], if Firefox is not available on the host platform.
 *
 * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]] and
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]] extend `BrowserFactory` and therefore require
 * you to fill in the `createWebDriver` method, usually by mixing in one of the `BrowserFactory` subtraits such as
 * `FirefoxFactory`.
 */
trait FirefoxFactory extends BrowserFactory {

  /**
   * [[FirefoxProfile]] that is used to create new instance of [[FirefoxDriver]].
   * Override to provide a different profile.
   */
  lazy val firefoxProfile: FirefoxProfile = new FirefoxProfile()

  /**
   * [[FirefoxOptions]] that is used to create new instance of [[FirefoxDriver]]. Override to provide
   * different options.
   */
  lazy val firefoxOptions: FirefoxOptions = new FirefoxOptions().setLogLevel(FirefoxDriverLogLevel.WARN)

  /**
   * Creates a new instance of a Selenium `FirefoxDriver` (using the `FirefoxProfile` provided by
   * the `firefoxProfile` field), or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `FirefoxDriver` or a `BrowserFactory.UnavailableDriver` if a Firefox
   * driver is not available on the host platform.
   */
  def createWebDriver(): WebDriver = FirefoxFactory.createWebDriver(firefoxProfile, firefoxOptions)
}

/**
 * Companion object to trait `FirefoxFactory` that mixes in the trait.
 */
object FirefoxFactory extends FirefoxFactory {

  // This factory method is used by FirefoxInfo in AllBrowsersPerTest.

  /**
   * Creates a new instance of a Selenium [[FirefoxDriver]], using the specified [[FirefoxProfile]],
   * or returns a [[BrowserFactory.UnavailableDriver]] that includes the exception that indicated the driver
   * was not supported on the host platform and an appropriate error message.
   *
   * @return a new instance of a Selenium [[FirefoxDriver]], using the specified [[FirefoxProfile]],
   *   or a [[BrowserFactory.UnavailableDriver]] if a Firefox driver is not available on the host platform.
   */
  def createWebDriver(firefoxProfile: FirefoxProfile): WebDriver = {
    createWebDriver(firefoxProfile, firefoxOptions)
  }

  /**
   * Creates a new instance of a Selenium [[FirefoxDriver]], using the specified [[FirefoxProfile]] and [[FirefoxOptions]],
   * or returns a [[BrowserFactory.UnavailableDriver]] that includes the exception that indicated the driver
   * was not supported on the host platform and an appropriate error message.
   *
   * @return a new instance of a Selenium [[FirefoxDriver]], using the specified [[FirefoxProfile]] and [[FirefoxOptions]],
   *   or a [[BrowserFactory.UnavailableDriver]] if a Firefox driver is not available on the host platform.
   */
  def createWebDriver(firefoxProfile: FirefoxProfile, options: FirefoxOptions): WebDriver = {
    try {
      val binary = new FirefoxBinary()
      new FirefoxDriver(
        options
          .setBinary(binary)
          .setLogLevel(FirefoxDriverLogLevel.WARN)
          .setProfile(firefoxProfile)
          .merge(new FirefoxOptions())
      )
    } catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreateFirefoxDriver", ex.getMessage))
    }
  }
}

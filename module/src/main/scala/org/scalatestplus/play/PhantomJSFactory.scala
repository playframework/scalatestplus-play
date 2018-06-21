/*
 * Copyright 2001-2016 Artima, Inc.
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
import BrowserFactory.UnavailableDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Factory whose `createWebDriver` method will either return a new Selenium `PhantomJSDriver` (created
 * using the capabilities specified by `phantomCapabilities`), or
 * [[org.scalatestplus.play.BrowserFactory.UnavailableDriver UnavailableDriver]], if Firefox is not available on the host platform.
 *
 * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]] and
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]] extend `BrowserFactory` and therefore require
 * you to fill in the `createWebDriver` method, usually by mixing in one of the `BrowserFactory` subtraits such as
 * `PhantomJSFactory`.
 */
@deprecated("PhantomJS is no longer actively developed, and support will eventually be dropped", "4.0.0")
trait PhantomJSFactory extends BrowserFactory {

  /**
   * 'DesiredCapabilities' that are used to create new instance of 'PhantomJSDriver'.
   * Override to provide a different capabilities.
   *
   * @see [[DesiredCapabilities]]
   */
  lazy val phantomCapabilities: DesiredCapabilities = DesiredCapabilities.phantomjs()

  /**
   * Creates a new instance of a Selenium `PhantomJSDriver` (using the `DesiredCapabilities` provided by
   * the `phantomCapabilities` field), or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `PhantomJSDriver` or a `BrowserFactory.UnavailableDriver` if a PhantomJS
   * driver is not available on the host platform.
   *
   * @see [[PhantomJSDriver]]
   */
  def createWebDriver(): WebDriver = PhantomJSFactory.createWebDriver(phantomCapabilities)
}

/**
 * Companion object to trait `PhantomJSFactory` that mixes in the trait.
 */
@deprecated("PhantomJS is no longer actively developed, and support will eventually be dropped", "4.0.0")
object PhantomJSFactory extends PhantomJSFactory {

  /**
   * Creates a new instance of a Selenium `PhantomJSDriver` (using the `DesiredCapabilities` provided by
   * the `phantomCapabilities` field), or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium [[PhantomJSDriver]] or a `BrowserFactory.UnavailableDriver` if a PhantomJS
   * driver is not available on the host platform.
   *
   * @see [[PhantomJSDriver]]
   */
  def createWebDriver(capabilities: DesiredCapabilities): WebDriver = {
    try {
      new PhantomJSDriver(capabilities)
    } catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreatePhantomJSDriver", ex.getMessage))
    }
  }
}

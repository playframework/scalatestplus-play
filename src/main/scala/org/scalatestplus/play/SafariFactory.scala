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
import org.openqa.selenium.safari.SafariDriver
import BrowserFactory.UnavailableDriver

/**
 * Trait providing a `createWebDriver` method that creates a new Selenium `SafariDriver`.
 */
trait SafariFactory extends BrowserFactory {

  /**
   * Creates a new instance of a Selenium `SafariDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `SafariDriver`, or a `BrowserFactory.UnavailableDriver` if a Safari driver is not 
   * available on the host platform.
   */
  def createWebDriver(): WebDriver =
    try {
      new SafariDriver()
    }
    catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreateSafariDriver"))
    }
}

/**
 * Companion object to trait `SafariFactory` that mixes in the trait.
 */
object SafariFactory extends SafariFactory


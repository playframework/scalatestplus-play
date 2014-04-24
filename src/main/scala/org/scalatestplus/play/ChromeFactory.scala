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
import org.openqa.selenium.chrome.ChromeDriver
import BrowserFactory.NoDriver

/**
 * Trait providing a `createWebDriver` method that creates a new Selenium `ChromeDriver`.
 */
trait ChromeFactory extends BrowserFactory {

  /**
   * Creates a new instance of a Selenium `ChromeDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.NoDriver BrowserFactory.NoDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `ChromeDriver`, or a `BrowserFactory.NoDriver` if it is not 
   * available on the host platform.
   */
  def createWebDriver(): WebDriver =
    try { 
      new ChromeDriver()
    }
    catch {
      case ex: Throwable => NoDriver(Some(ex), Resources("cantCreateChromeDriver"))
    }
}

/**
 * Companion object to trait `ChromeFactory` that mixes in the trait.
 */
object ChromeFactory extends ChromeFactory


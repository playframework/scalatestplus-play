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
import BrowserFactory.NoDriver

/**
 * Trait providing a 'createWebDriver' method that creates a new Selenium 'FirefoxDriver'.
 */
trait FirefoxFactory extends BrowserFactory {

  /**
   * 'FirefoxProfile' that is used to create new instance of 'FirefoxDriver'.
   * Override to provide a different `FirefoxProfile`.
   */
  val firefoxProfile = new FirefoxProfile()

  /**
   * Creates a new instance of 'FirefoxDriver'.
   */
  def createWebDriver(): WebDriver =
    try {
      new FirefoxDriver(firefoxProfile)
    }
    catch {
      case ex: Throwable => NoDriver(Some(ex), Resources("cantCreateFirefoxDriver"))
    }
    
  // Use inherited Scaladoc message
  // def unableToCreateDriverErrorMessage: String = 
}

object FirefoxFactory extends FirefoxFactory {
  // This one uses the passed firefoxProfile instead of the field. It is used
  // by FirefoxInfo in AllBrowsersPerSharedTest.
  def createWebDriver(firefoxProfile: FirefoxProfile): WebDriver =
    try {
      new FirefoxDriver(firefoxProfile)
    }
    catch {
      case ex: Throwable => NoDriver(Some(ex), Resources("cantCreateFirefoxDriver"))
    }
}

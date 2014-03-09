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

/**
 * Trait providing a <code>createNewDriver</code> method that creates a new Selenium <code>FirefoxDriver</code>.
 */
trait FirefoxBrowser extends BrowserDriver {
  /**
   * <code>FirefoxProfile</code> that is used to create new instance of <code>FirefoxDriver</code>.
   */
  val firefoxProfile = new FirefoxProfile()

  /**
   * Creates a new instance of <code>FirefoxDriver</code>.
   */
  def createNewDriver: WebDriver = new FirefoxDriver(firefoxProfile)
    
  // Use inherited Scaladoc message
  def cantCreateRequestedDriver: String = Resources("cantCreateFirefoxDriver")
}


/*
 * Copyright 2016 Lightbend.
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

import com.machinepublishers.jbrowserdriver.{ JBrowserDriver, Settings }
import org.openqa.selenium.WebDriver
import org.scalatestplus.play.BrowserFactory.UnavailableDriver

/**
 * Factory whose `createWebDriver` method will either return a new Selenium `JBrowserDriverFactory`, or
 * [[org.scalatestplus.play.BrowserFactory.UnavailableDriver UnavailableDriver]], if Chrome is not available on the host platform.
 *
 * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]] and
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]] extend `BrowserFactory` and therefore require
 * you to fill in the `createWebDriver` method, usually by mixing in one of the `BrowserFactory` subtraits such as
 * `JBrowserDriverFactory`.
 */
trait JBrowserDriverFactory extends BrowserFactory {

  lazy val settings: Settings = {
    Settings.builder()
      .headless(true)
      .javascript(true)
      .javaOptions(
        "-Dprism.verbose=true",
        "-Dprism.useFontConfig=false"
      ).build()
  }

  /**
   * Creates a new instance of a Selenium `JBrowserDriver`, or returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `JBrowserDriver`, or a `BrowserFactory.UnavailableDriver` if it is not
   * available on the host platform.
   */
  def createWebDriver(): WebDriver =
    try {
      new JBrowserDriver(settings)
    } catch {
      case ex: Throwable => UnavailableDriver(Some(ex), Resources("cantCreateJBrowserDriver", ex.getMessage))
    }
}

object JBrowserDriverFactory extends JBrowserDriverFactory

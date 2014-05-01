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
import org.openqa.selenium.WebDriver

/**
 * Trait that defines an abstract `createWebDriver`  method for creating a new Selenium `WebDriver`
 * and an abstract `unableToCreateDriverErrorMessage` method that provides an appropriate error message if the driver
 * is not available on the current platform.
 *
 * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]] and 
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]] extend `BrowserFactory` and therefore require
 * you to fill in the `createWebDriver` method, usually by mixing in one of the `BrowserFactory` subtraits.
 */
trait BrowserFactory {

  /**
   * Creates a new instance of a valid Selenium `WebDriver`, or if a driver is unavailable on the host platform,
   * returns a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]] that includes
   * the exception that indicated the driver was not supported on the host platform and an appropriate
   * error message.
   *
   * @return an new instance of a Selenium `WebDriver`, or a [[org.scalatestplus.play.BrowserFactory.UnavailableDriver BrowserFactory.UnavailableDriver]]
   * if the desired `WebDriver` is not available on the host platform.
   */
  def createWebDriver(): WebDriver
}

import org.openqa.selenium._

/**
 * Companion object to trait `BrowserFactory` that holds a `UnavailableDriver` object that implements 
 * the Selenium `WebDriver` interface by throwing `UnuspportedOperationException`. This is
 * used as a placeholder when a driver is not available on the host platform.
 */
object BrowserFactory {

  /**
   * A null-object implementation of the Selenium `WebDriver` interface that throws `UnsupportedOperationException` from
   * all of its methods, used when either 1) a `WebDriver` field has not yet been initialized, 2) a requested Selenium
   * `WebDriver` is unavailable on the host platform, or 3) a test that did not declare it needed a `WebDriver` in
   * [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowserPerSuite]] or [[org.scalatestplus.play.AllBrowsersPerTest AllBrowserPerTest]] attempts to use one.
   *
   * This is named `GrumpyDriver` because all it does is complain. No matter what you ask of it, it throws an
   * `UnsupportedOperationException` back at you.
   */
  sealed abstract class GrumpyDriver extends WebDriver {

    /**
     * Throws `UnsupportedOperationException` with an appropriate error message and, optionally, cause.
     */
    protected def complain(): Nothing

    /**
     * Throws `UnsupportedOperationException`.
     */
    final def close(): Unit = complain()

    /**
     * Throws `UnsupportedOperationException`.
     */
    final def findElement(by: By): WebElement = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def findElements(by: By): java.util.List[WebElement] = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def get(url: String): Unit = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def getCurrentUrl(): String = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def getPageSource(): String = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def getTitle(): String = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def getWindowHandle(): String = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def getWindowHandles(): java.util.Set[java.lang.String] = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def manage(): WebDriver.Options = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def navigate(): WebDriver.Navigation = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def quit(): Unit = complain()
  
    /**
     * Throws `UnsupportedOperationException`.
     */
    final def switchTo(): WebDriver.TargetLocator = complain()
  }

  /**
   * An implementation of `WebDriver` that provides an optional exception and an error message and throws `UnsupportedOperationException` from
   * all of its other methods, used when a requested Selenium `WebDriver` is unavailable on the host platform.
   *
   * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]], [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]],
   * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]], and [[org.scalatestplus.play.MixedFixtures MixedFixtures]] check
   * if the requested `WebDriver` is available, and if not, installs this driver (to avoid initializing with `null`)
   * and cancels the tests.
   *
   * This is an example of the "Null Object Pattern." We use this pattern to avoid initializing with `null` instead of making the driver type
   * an `Option[WebDriver]` for two reasons: 1) the type of the implicit needed by Selenium is `WebDriver`, not `Option[WebDriver]`, and 2) 
   * the Null Object we provide also carries an optional exception and user-friendly error message.
   *
   * @param ex: the `Throwable`, if any, that was thrown when attempting to use the requested driver
   * @param errorMessage: a user-friendly error message that mentions the specific driver that was unavailable on the host platform
   */
  case class UnavailableDriver(ex: Option[Throwable], errorMessage: String) extends GrumpyDriver {
    protected def complain(): Nothing =
      ex match {
        case Some(cause) => 
          throw new UnsupportedOperationException(errorMessage, cause)
        case None =>
          throw new UnsupportedOperationException(errorMessage)
      }
  }

  /**
   * An implementation of `WebDriver` that throws `UnsupportedOperationException` from
   * all of its methods, used when a test does not need a Selenium `WebDriver` at all.
   *
   * Traits [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]], [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]],
   * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]], and [[org.scalatestplus.play.MixedFixtures MixedFixtures]] check
   * if the requested `WebDriver` is available, and if not, installs this driver (to avoid initializing with `null`)
   * and cancels the tests.
   *
   * This is an example of the "Null Object Pattern." We use this pattern to avoid initializing with `null` instead of making the driver type
   * an `Option[WebDriver]` for two reasons: 1) the type of the implicit needed by Selenium is `WebDriver`, not `Option[WebDriver]`, and 2) 
   * the `UnsupportedOperationException` thrown by the methods of the Null Object we provide carries a user-friendly error message.
   */
  case object UnneededDriver extends GrumpyDriver {
    protected def complain(): Nothing = throw new UnsupportedOperationException(Resources("webDriverUsedFromUnsharedTest"))
  }

  /**
   * An implementation of `WebDriver` that throws `UnsupportedOperationException` from
   * all of its methods, used to initialize instance `var`s of type `WebDriver`.
   *
   * Traits [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]], [[org.scalatestplus.play.ConfiguredBrowser ConfiguredBrowser]],
   * [[org.scalatestplus.play.AllBrowsersPerSuite AllBrowsersPerSuite]], and [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]] initialize
   * their `webDriver` field with this value (to avoid initializing with `null`).
   *
   * This is an example of the "Null Object Pattern." We use this pattern to avoid initializing with `null` instead of making the driver type
   * an `Option[WebDriver]` for two reasons: 1) the type of the implicit needed by Selenium is `WebDriver`, not `Option[WebDriver]`, and 2) 
   * the `UnsupportedOperationException` thrown by the methods of the Null Object we provide carries a user-friendly error message.
   */
  case object UninitializedDriver extends GrumpyDriver {
    protected def complain(): Nothing = throw new UnsupportedOperationException(Resources("webDriverUninitialized"))
  }
}

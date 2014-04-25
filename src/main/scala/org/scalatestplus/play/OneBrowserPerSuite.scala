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
import BrowserFactory.NoDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Trait that provides one `WebBrowser` instance per ScalaTest `Suite`.
 * 
 * It overrides ScalaTest's `Suite.run` method to start a `TestServer` before test execution, 
 * and stop the `TestServer` after test execution has completed.  You can access the `FakeApplication`
 * in `args.configMap` using the `"org.scalatestplus.play.app"` key, the port number of the `TestServer` using the `"org.scalatestplus.play.port"` key and 
 * the `WebDriver` instance using `"org.scalatestplus.play.webDriver"` key.  This traits also overrides `Suite.withFixture` 
 * to cancel all the tests automatically if the related `WebDriver` is not available in the running system.
 */
trait OneBrowserPerSuite extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience with BrowserFactory { this: Suite =>

  /**
   * An implicit instance of `FakeApplication`.
   *
   * This trait's implementation initializes this `lazy` `val` with a new instance of `FakeApplication` with
   * parameters set to their defaults. Override this `lazy` `val` if you need a `FakeApplication` created with non-default parameter values.
   */
  implicit lazy val app: FakeApplication = new FakeApplication()

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  lazy val port: Int = Helpers.testServerPort

  /**
   * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
   * will be same as the value of `port`.
   */
  implicit final lazy val portNumber: PortNumber = PortNumber(port)

  /**
   * An implicit instance of `WebDriver`, created by calling `createWebDriver`.  
   * If there is error when creating the `WebDriver`, `NoDriver` will be assigned 
   * instead.
   */
  implicit val webDriver: WebDriver = createWebDriver()
  // try { createNewDriver } catch { case ex: Throwable => NoDriver(Some(ex)) }

  /**
   * Override to cancel tests automatically when `webDriver` resolve to `NoDriver`
   */
  abstract override def withFixture(test: NoArgTest): Outcome = {
    webDriver match {
      case NoDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
      case _ => super.withFixture(test)
    }
  }

  /**
   * Overriden to start `TestServer` before running the tests, pass a `FakeApplication` into the tests in 
   * `args.configMap` via "org.scalatestplus.play.app" key, `TestServer`'s port number via "org.scalatestplus.play.port" and `WebDriver` 
   * instance via "org.scalatestplus.play.webDriver" key.  It then calls `super.run` to execute the tests, and upon completion stops `TestServer` 
   * and close the `WebDriver`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    val testServer = TestServer(port, app)
    try {
      testServer.start()
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app) + ("org.scalatestplus.play.port" -> port) + ("org.scalatestplus.play.webDriver" -> webDriver)
      val newArgs = args.copy(configMap = newConfigMap)
      super.run(testName, newArgs)
    } finally {
      testServer.stop()
      webDriver match {
        case _: NoDriver => // do nothing for NoDriver
        case safariDriver: SafariDriver => safariDriver.quit()
        case chromeDriver: ChromeDriver => chromeDriver.quit()
        case _ => webDriver.close()
      }
    }
  }
}


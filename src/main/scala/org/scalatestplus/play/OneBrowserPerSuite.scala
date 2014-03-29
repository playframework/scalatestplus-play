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
 * Trait that provides one <code>WebBrowser</code> instance per ScalaTest <code>Suite</code>.
 * 
 * It overrides ScalaTest's <code>Suite.run</code> method to start a <code>TestServer</code> before test execution, 
 * and stop the <code>TestServer</code> after test execution has completed.  You can access the <code>FakeApplication</code>
 * in <code>args.configMap</code> using the <code>"org.scalatestplus.play.app"</code> key, the port number of the <code>TestServer</code> using the <code>"org.scalatestplus.play.port"</code> key and 
 * the <code>WebDriver</code> instance using <code>"org.scalatestplus.play.webDriver"</code> key.  This traits also overrides <code>Suite.withFixture</code> 
 * to cancel all the tests automatically if the related <code>WebDriver</code> is not available in the running system.
 */
trait OneBrowserPerSuite extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience with BrowserFactory { this: Suite =>

  /**
   * An implicit instance of <code>FakeApplication</code>.
   */
  implicit val app: FakeApplication = new FakeApplication()

  /**
   * The port used by the <code>TestServer</code>.  By default this will be set to the result return from 
   * <code>Helpers.testServerPort</code>, user can override this to provide their own port number.
   */
  val port: Int = Helpers.testServerPort

  /**
   * An implicit instance of <code>WebDriver</code>, created by calling <code>createNewDriver</code>.  
   * If there is error when creating the <code>WebDriver</code>, <code>NoDriver</code> will be assigned 
   * instead.
   */
  implicit val webDriver: WebDriver = try { createNewDriver } catch { case ex: Throwable => NoDriver(Some(ex)) }

  /**
   * Override to cancel tests automatically when <code>webDriver</code> resolve to <code>NoDriver</code>
   */
  abstract override def withFixture(test: NoArgTest): Outcome = {
    webDriver match {
      case NoDriver(ex) =>
          ex match {
            case Some(e) => cancel(cantCreateRequestedDriver, e)
            case None => cancel(cantCreateRequestedDriver)
          }
      case _ => super.withFixture(test)
    }
  }

  /**
   * Overriden to start <code>TestServer</code> before running the tests, pass a <code>FakeApplication</code> into the tests in 
   * <code>args.configMap</code> via "org.scalatestplus.play.app" key, <code>TestServer</code>'s port number via "org.scalatestplus.play.port" and <code>WebDriver</code> 
   * instance via "org.scalatestplus.play.webDriver" key.  It then calls <code>super.run</code> to execute the tests, and upon completion stops <code>TestServer</code> 
   * and close the <code>WebDriver</code>.
   *
   * @param testName an optional name of one test to run. If <code>None</code>, all relevant tests should be run.
   *                 I.e., <code>None</code> acts like a wildcard that means run all relevant tests in this <code>Suite</code>.
   * @param args the <code>Args</code> for this run
   * @return a <code>Status</code> object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
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
        case NoDriver(_) => // do nothing for NoDriver
        case safariDriver: SafariDriver => safariDriver.quit()
        case chromeDriver: ChromeDriver => chromeDriver.quit()
        case _ => webDriver.close()
      }
    }
  }
}


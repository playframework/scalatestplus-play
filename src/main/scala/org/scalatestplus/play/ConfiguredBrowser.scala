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
import play.api.Play
import selenium.WebBrowser
import org.openqa.selenium.WebDriver
import concurrent.Eventually
import concurrent.IntegrationPatience

/**
 * Trait that provides a configured `FakeApplication`, server port number, and Selenium `WebDriver` to the suite
 * into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]]
 * to make use of the `FakeApplication`, port number, and `WebDriver` provided by `OneBrowserPerSuite`. Trait `OneBrowserPerSuite` will ensure
 * the `FakeApplication` is placed in the `ConfigMap` under the key `org.scalatestplus.play.app`, the port number
 * under the key `org.scalatestplus.play.port`, and the `WebDriver` under the key `org.scalatestplus.play.webDriver` before nested suites are invoked. This
 * information represents the "configured browser" that is passed from the enclosing suite to the nested suites. Trait `ConfiguredBrowser` extracts this information from
 * from the `ConfigMap` and makes the `FakeApplication` available via the `app` method, the port number available as an `Int` from
 * the `port` method, and also the port number wrapped in a [[org.scalatestplus.play.PortNumber PortNumber]] available as implicit method `portNumber` (for use
 * with trait [[org.scalatestplus.play.WsScalaTestClient WsScalaTestClient]]), and the `WebDriver` available implicitly from the `webDriver` method.
 *
 * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`.
 */
trait ConfiguredBrowser extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite with ServerProvider => 

  private var configuredWebDriver: WebDriver = _

  /**
   * The "configured" Selenium `WebDriver`, passed into `run` via the `ConfigMap`.
   *
   * @return the configured port number
   */
  implicit def webDriver: WebDriver = synchronized { configuredWebDriver } 

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.app" whose value is a `FakeApplication`, 
   * a key named "org.scalatestplus.play.port" whose value is an `Int`,
   * and a key named "org.scalatestplus.play.webDriver" whose value is a `WebDriver`,
   * and if they exist, sets the `FakeApplication` as the value that will be returned from the `app` method,
   * the `Int` as the value that will be returned from the `port` method, and the `WebDriver` as
   * the value that will be returned from the `webDriver` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.app" in `args.configMap`, or the associated value is
   * not a `FakeApplication`, or if no key matches "org.scalatestplus.play.port" in `args.configMap`,
   * or the associated value is not an `Int`, or if no key matches "org.scalatestplus.play.webDriver" in `args.configMap`,
   * or the associated value is not a `WebDriver`, throws `IllegalArgumentException`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws IllegalArgumentException if the `FakeApplication` and/or port number does not appear in `args.configMap` under the expected keys
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[WebDriver]("org.scalatestplus.play.webDriver") match {
      case Some(cwd) => synchronized { configuredWebDriver = cwd }
      case None => throw new Exception("ConfiguredBrowser needs a WebDriver value associated with key \"org.scalatestplus.play.webDriver\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


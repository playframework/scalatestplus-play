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

/**
 * Trait that provides one `TestServer` and `FakeApplication` instance per ScalaTest `Suite`.
 * 
 * It overrides ScalaTest's `Suite.run` method to start a `TestServer` before test execution, 
 * and stop the `TestServer` automatically after test execution has completed. 
 * In the suite that mixes in `OneServerPerSuite`,
 * you can access the `FakeApplication` via the `app` field and the port used by the `TestServer`
 * via the `port`field. In nested suites,
 * you can access the `FakeApplication` and port number from the `args.configMap`, where they are associated
 * with keys `"org.scalatestplus.play.app"` and `"org.scalatestplus.play.port"`, respectively.
 */
trait OneServerPerSuite extends SuiteMixin { this: Suite =>

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
   * Overriden to start `TestServer` before running the tests, pass a `FakeApplication` into the tests in 
   * `args.configMap` via "org.scalatestplus.play.app" key and port used by the started `TestServer` via the "org.scalatestplus.play.port" key.  It then calls 
   * `super.run` to execute the tests and stop `TestServer` automatically after test executions.
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
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app) + ("org.scalatestplus.play.port" -> port)
      val newArgs = args.copy(configMap = newConfigMap)
      super.run(testName, newArgs)
    } finally testServer.stop()
  }
}


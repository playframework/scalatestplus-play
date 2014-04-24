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
 * Trait that provides a new `FakeApplication` and running `TestServer` instance for each test executed in a ScalaTest `Suite`.
 * 
 * This trait overrides ScalaTest's `withFixture` method to create a new `FakeApplication` and `TestServer`
 * before each test, and ensure they are cleaned up after the test has completed. The `FakeApplication` is available (implicitly) from
 * method `app`. The `TestServer`'s port number is available as `port` (and implicitly available as `portNumber`, wrapped in a [[org.scalatestplus.play.PortNumber PortNumber]]).
 */
trait OneServerPerTest extends SuiteMixin { this: Suite =>

  private var privateApp: FakeApplication = _

  /**
   * Implicit method that returns the `FakeApplication` instance for the current test.
   */
  implicit def app: FakeApplication = synchronized { privateApp }

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  val port: Int = Helpers.testServerPort

  /**
   * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
   * will be same as the value of `port`.
   */
  implicit lazy val portNumber: PortNumber = PortNumber(port)

  /**
   * Creates a new `FakeApplication` and running `TestServer` instance before executing each test, and 
   * ensure they are cleaned up after the test completes. You can access the `FakeApplication` from
   * your tests via `app` and the `TestServer`'s port number via `port`.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized { privateApp = new FakeApplication() }
    Helpers.running(TestServer(port, app)) {
      super.withFixture(test)
    }
  }
}


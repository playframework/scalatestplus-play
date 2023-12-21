/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalatestplus.play

import play.api.Application
import play.api.test.RunningServer

/**
 * Trait that defines abstract methods that providing a port number and implicit `Application` and a concrete
 * method that provides an implicit [[org.scalatestplus.play.PortNumber PortNumber]] that wraps the port number.
 *
 * This trait is implemented by [[org.scalatestplus.play.BaseOneServerPerSuite OneServerPerSuite]],
 * [[org.scalatestplus.play.OneServerPerTest OneServerPerTest]], and
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredServer]], each of which use a different strategy to
 * provide `TestServer`s to tests. This trait is included in the self-type of
 * [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]], and
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]], and
 * [[org.scalatestplus.play.AllBrowsersPerTest AllBrowsersPerTest]], allowing you to select
 * the `WebDriver` strategy (i.e., the extent to which `WebDriver`s are shared between tests) independently from the
 * `TestServer` strategy (the extent to which `TestServer`s are shared between tests).
 */
trait ServerProvider {

  /**
   * Implicit method that returns a `Application` instance.
   */
  implicit def app: Application

  protected implicit def runningServer: RunningServer

  /**
   * The port used by the `TestServer`.
   */
  // TODO: Document that this has been converted to a final method
  final def port: Int = portNumber.value

  /**
   * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
   * will be same as the value of `port`.
   *
   * @return the configured port number, wrapped in a `PortNumber`
   */
  implicit def portNumber: PortNumber = {
    val port = runningServer.endpoints.httpEndpoint
      .orElse(runningServer.endpoints.httpsEndpoint)
      .fold(throw new IllegalStateException("Nor HTTP or HTTPS port available for test server"))(_.port)
    PortNumber(port)
  }
}

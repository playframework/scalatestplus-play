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

import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.mvc.Call

/**
 * Trait providing convenience methods to create WS requests in tests.
 */
trait WsScalaTestClient {

  /**
   * Construct a WS request for the given reverse route.
   *
   * For example:
   * {{{
   *   wsCall(controllers.routes.Application.index()).get()
   * }}}
   *
   * @param call the `Call` describing the request
   * @param portNumber the port number of the `TestServer`
   * @param wsClient the implicit WSClient
   */
  def wsCall(call: Call)(implicit portNumber: PortNumber, wsClient: WSClient): WSRequest =
    doCall(call.url, wsClient, portNumber)

  /**
   * Construct a WS request for the given relative URL.
   *
   * @param url the URL of the request
   * @param portNumber the port number of the `TestServer`
   * @param wsClient the implicit WSClient
   */
  def wsUrl(url: String)(implicit portNumber: PortNumber, wsClient: WSClient): WSRequest =
    doCall(url, wsClient, portNumber)

  /**
   * Construct a WS request for the given relative URL.
   *
   * @param url the URL of the request
   * @param secure if the request should be send via https
   * @param portNumber the port number of the `TestServer`
   * @param wsClient the implicit WSClient
   */
  def wsUrl(url: String, secure: Boolean)(implicit portNumber: PortNumber, wsClient: WSClient): WSRequest =
    doCall(url, wsClient, portNumber, secure)

  private def doCall(url: String, wsClient: WSClient, portNumber: PortNumber, secure: Boolean = false) = {
    wsClient.url((if (secure) "https" else "http") + "://localhost:" + portNumber.value + url)
  }
}

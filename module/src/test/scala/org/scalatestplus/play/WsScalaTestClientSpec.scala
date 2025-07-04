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

import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.mvc.Call
import play.api.inject.guice.*
import play.api.libs.ws.WSClient

class WsScalaTestClientSpec extends UnitSpec with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience {

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure("foo" -> "bar")
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  "WsScalaTestClient's" must {

    "wsUrl works correctly" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
      val futureResult          = wsUrl("/testing").get()
      val body                  = futureResult.futureValue.body
      val expectedBody          =
        "<html>" +
          "<head><title>Test Page</title></head>" +
          "<body>" +
          "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
          "</body>" +
          "</html>"
      assert(body == expectedBody)
    }

    "wsCall works correctly" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])

      val futureResult = wsCall(Call("get", "/testing")).get()
      val body         = futureResult.futureValue.body
      val expectedBody =
        "<html>" +
          "<head><title>Test Page</title></head>" +
          "<body>" +
          "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
          "</body>" +
          "</html>"
      assert(body == expectedBody)
    }
  }

}

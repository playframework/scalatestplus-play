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

package org.scalatestplus.play.examples.guice.oneserverpersuite

import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.*

class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite {

  // Override fakeApplication() if you need a Application with other than non-default parameters.
  implicit override def fakeApplication(): Application = {
    GuiceApplicationBuilder().configure("foo" -> "bar").build()
  }

  "The OneServerPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)
      getConfig("foo") mustBe Some("bar")
    }
    "provide an http endpoint" in {
      runningServer.endpoints.httpEndpoint must not be empty
    }
    "provide an actual running server" in {
      import java.net.*
      val url = new URI("http://localhost:" + port + "/boum").toURL
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
}

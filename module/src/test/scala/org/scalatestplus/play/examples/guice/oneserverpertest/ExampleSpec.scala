/*
 * Copyright 2001-2016 Artima, Inc.
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
package org.scalatestplus.play.examples.guice.oneserverpertest

import play.api.test._
import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.{ Play, Application }
import play.api.inject.guice._

class ExampleSpec extends PlaySpec with GuiceOneServerPerTest {

  // Override newAppForTest if you need a test with other than non-default parameters, or use GuiceOneServerPerTest.
  override def newAppForTest(testData: TestData): Application = {
    new GuiceApplicationBuilder()
      .configure(Map("ehcacheplugin" -> "disabled"))
      .router(TestRoutes.router)
      .build()
  }

  "The OneServerPerTest trait" must {
    "provide a FakeApplication" in {
      app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the FakeApplication available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the FakeApplication" in {
      Play.maybeApplication mustBe Some(app)
    }
    "provide the port number" in {
      port mustBe Helpers.testServerPort
    }
    "provide an actual running server" in {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boum")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
}

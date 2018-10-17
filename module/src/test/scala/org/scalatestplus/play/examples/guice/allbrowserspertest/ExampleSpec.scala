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
package org.scalatestplus.play.examples.guice.allbrowserspertest

import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.inject.guice._

class ExampleSpec extends PlaySpec with GuiceOneServerPerTest with AllBrowsersPerTest {

  // Override newAppForTest if you need a Application with other than non-default parameters.
  override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .configure("foo" -> "bar")
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  // Place tests you want run in different browsers in the `sharedTests` method:
  def sharedTests(browser: BrowserInfo) = {

    "The AllBrowsersPerTest trait" must {
      "provide a web driver " + browser.name in {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click on find(name("b")).value
        eventually { pageTitle mustBe "scalatest" }
      }
    }
  }

  // Place tests you want run just once outside the `sharedTests` method
  // in the constructor, the usual place for tests in a `PlaySpec`
  "The AllBrowsersPerTest trait" must {
    "provide a FakeApplication" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
      getConfig("foo") mustBe Some("bar")
    }
    "provide an http endpoint" in {
      runningServer.endpoints.httpEndpoint must not be empty
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

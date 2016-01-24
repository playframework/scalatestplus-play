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
package org.scalatestplus.play.examples.allbrowserspersuite

import play.api.test._
import org.scalatestplus.play._
import play.api.{Play, Application}
import play.api.inject.guice._
import play.api.routing._
import play.api.cache.EhCacheModule
 
class ExampleSpec extends PlaySpec with OneServerPerSuite with AllBrowsersPerSuite {

  // Override app if you need an Application with other than
  // default parameters.
  implicit override lazy val app =
    new GuiceApplicationBuilder().disable[EhCacheModule].configure("foo" -> "bar").additionalRouter(Router.from(TestRoute)).build()

  // Place tests you want run in different browsers in the `sharedTests` method:
  def sharedTests(browser: BrowserInfo) = {

    "The AllBrowsersPerSuite trait" must {
      "provide a web driver " + browser.name in {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click on find(name("b")).value
        eventually { pageTitle mustBe "scalatest" }
      }
    }
  }

  // Place tests that don't need a WebDriver outside the `sharedTests` method
  // in the constructor, the usual place for tests in a `PlaySpec`
  "The AllBrowsersPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in {
      Play.maybeApplication mustBe Some(app)
    }
    "provide the port" in {
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

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
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice._

class OneServerPerSuiteWithOneBrowserPerTestSpec
    extends UnitSpec
    with GuiceOneServerPerSuite
    with OneBrowserPerTest
    with IntegrationPatience
    with FirefoxFactory {

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure("foo" -> "bar")
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  "The OneBrowserPerTest trait" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      getConfig("foo") mustBe Some("bar")
    }
    "provide an http endpoint" in {
      runningServer.endpoints.httpEndpoint must not be empty
    }
    "send 404 on a bad request" in {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boum")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
}

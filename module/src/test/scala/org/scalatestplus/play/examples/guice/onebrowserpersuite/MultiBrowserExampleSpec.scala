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
package org.scalatestplus.play.examples.guice.onebrowserpersuite

import play.api.test._
import org.scalatest._
import tags._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice._

// Place your tests in an abstract class
abstract class MultiBrowserExampleSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite {

  // Override app if you need an Application with other than non-default parameters.
  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure("foo" -> "bar", "ehcacheplugin" -> "disabled")
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  "The OneBrowserPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)
      getConfig("ehcacheplugin") mustBe Some("disabled")
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
    "provide a web driver" in {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
}

// Then make a subclass that mixes in the factory for each
// Selenium driver you want to test with.
@FirefoxBrowser class FirefoxExampleSpec extends MultiBrowserExampleSpec with FirefoxFactory
@SafariBrowser class SafariExampleSpec extends MultiBrowserExampleSpec with SafariFactory
@InternetExplorerBrowser class InternetExplorerExampleSpec extends MultiBrowserExampleSpec with InternetExplorerFactory
@ChromeBrowser class ChromeExampleSpec extends MultiBrowserExampleSpec with ChromeFactory
@HtmlUnitBrowser class HtmlUnitExampleSpec extends MultiBrowserExampleSpec with HtmlUnitFactory


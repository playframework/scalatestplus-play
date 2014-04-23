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
import play.api.{Play, Application}
import play.api.mvc.{Action, Results}
import org.openqa.selenium.WebDriver
import BrowserFactory.NoDriver

class AllBrowsersPerSharedTestSpec extends UnitSpec with AllBrowsersPerSharedTest {

  implicit override def app: FakeApplication =
    FakeApplication(
      additionalConfiguration = Map("foo" -> "bar", "ehcacheplugin" -> "disabled"),
      withRoutes = TestRoute
    )
  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  // Doesn't need synchronization because set by withFixture and checked by the test
  // invoked inside same withFixture with super.withFixture(test)
  var configMap: ConfigMap = _

  override def withFixture(test: NoArgTest): Outcome = {
    configMap = test.configMap
    super.withFixture(test)
  }

  def sharedTests(browser: BrowserInfo) = {

    "The AllBrowsersPerSharedTest trait" must {
      "put the webDriver in the configMap " + browser.name in {
        val configuredWebDriver = configMap.getOptional[WebDriver]("org.scalatestplus.play.webDriver")
        configuredWebDriver mustBe defined
      }
      "put the webDriverName in the configMap " + browser.name in {
        val configuredWebDriverName = configMap.getOptional[String]("org.scalatestplus.play.webDriverName")
        configuredWebDriverName mustBe defined
      }
      "provide a web driver " + browser.name in {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click on find(name("b")).value
        eventually { pageTitle mustBe "scalatest" }
      }
    }
  }

  "The AllBrowsersPerSharedTest trait" must {
    "provide a FakeApplication" in {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in {
      Play.maybeApplication mustBe Some(app)
    }
    "provide the port" in {
      port mustBe Helpers.testServerPort
    }
    "send 404 on a bad request" in {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boum")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "put the app in the configMap" in {
      val configuredApp = configMap.getOptional[FakeApplication]("org.scalatestplus.play.app")
      configuredApp mustBe defined
    }
    "put the port in the configMap" in {
      val configuredPort = configMap.getOptional[Int]("org.scalatestplus.play.port")
      configuredPort.value mustEqual port
    }
    "not put the webDriver in the configMap" in {
      val configuredWebDriver = configMap.getOptional[WebDriver]("org.scalatestplus.play.webDriver")
      configuredWebDriver mustBe None
    }
    "not put the webDriverName in the configMap" in {
      val configuredWebDriverName = configMap.getOptional[String]("org.scalatestplus.play.webDriverName")
      configuredWebDriverName mustBe None
    }
    "provide a NoDriver that provides an error message with a hint to put the test into the sharedTests method" in {
      inside(webDriver) { case NoDriver(_, errorMessage) => 
        errorMessage mustBe Resources("webDriverUsedFromUnsharedTest")
      }
    }
  }
}

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
package org.scalatestplus.play

import play.api.test._
import org.scalatest._
import play.api.{ Play, Application }
import play.api.inject.guice._
import play.api.routing._

class MixedPlaySpecSpec extends MixedPlaySpec { thisSpec =>

  "MixedPlaySpec" must {
    "mix in OptionValues" in { () =>
      thisSpec mustBe an[OptionValues]
    }
    "mix in MixedFixtures" in { () =>
      thisSpec mustBe an[MixedFixtures]
    }
  }

  def buildApp[A](elems: (String, String)*) =
    new GuiceApplicationBuilder().configure(Map(elems: _*)).router(TestRoutes.router).build()

  def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)

  "The App function" must {
    "provide an Application" in new App(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new App(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new App(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
  }
  "The Server function" must {
    "provide an Application" in new Server(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Server(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Server(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    import Helpers._
    "send 404 on a bad request" in new Server {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
  "The HtmlUnit function" must {
    "provide an Application" in new HtmlUnit(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new HtmlUnit(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    import Helpers._
    "send 404 on a bad request" in new HtmlUnit {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new HtmlUnit(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Firefox function" must {
    "provide an Application" in new Firefox(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Firefox(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Firefox(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    import Helpers._
    "send 404 on a bad request" in new Firefox {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Firefox(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Safari(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Safari(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    import Helpers._
    "send 404 on a bad request" in new Safari {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Safari(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Chrome(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Chrome(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    import Helpers._
    "send 404 on a bad request" in new Chrome {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Chrome(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new InternetExplorer(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    import Helpers._
    "send 404 on a bad request" in new InternetExplorer {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new InternetExplorer(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
}


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

import org.scalatest._
import play.api.Application
import play.api.inject.guice._

class MixedPlaySpecSpec extends MixedPlaySpec { thisSpec =>

  "MixedPlaySpec" must {
    "mix in OptionValues" in { () =>
      assert(thisSpec.isInstanceOf[OptionValues])
    }
    "mix in MixedFixtures" in { () =>
      assert(thisSpec.isInstanceOf[MixedFixtures])
    }
  }

  def buildApp[A](elems: (String, String)*): Application = {
    GuiceApplicationBuilder()
      .configure(Map(elems: _*))
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  "The App function" must {
    "provide an Application" in new App(buildApp("foo" -> "bar")) {
      override def running() =
        app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new App(buildApp("foo" -> "bar")) {
      override def running() =
        getConfig("foo") mustBe Some("bar")
    }
  }
  "The Server function" must {
    "provide an Application" in new Server(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Server(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "send 404 on a bad request" in new Server {
      override def running() = {
        import java.net._
        val url                    = new URL("http://localhost:" + port + "/boom")
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
  }
  "The HtmlUnit function" must {
    "provide an Application" in new HtmlUnit(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "send 404 on a bad request" in new HtmlUnit {
      override def running() = {
        import java.net._
        val url                    = new URL("http://localhost:" + port + "/boom")
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new HtmlUnit(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The Firefox function" must {
    "provide an Application" in new Firefox(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Firefox(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "send 404 on a bad request" in new Firefox {
      override def running() = {
        import java.net._
        val url                    = new URL("http://localhost:" + port + "/boom")
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new Firefox(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Safari(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "send 404 on a bad request" in new Safari {
      override def running() = {
        import java.net._
        val url                    = new URL("http://localhost:" + port + "/boom")
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new Safari(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Chrome(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "send 404 on a bad request" in new Chrome {
      override def running() = {
        import java.net._
        val url                    = new URL("http://localhost:" + port + "/boom")
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new Chrome(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "send 404 on a bad request" in new InternetExplorer {
      override def running() = {
        import java.net._
        val url                    = new URL("http://localhost:" + port + "/boom")
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new InternetExplorer(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
}

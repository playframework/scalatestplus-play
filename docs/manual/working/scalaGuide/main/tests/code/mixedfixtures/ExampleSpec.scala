/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package org.scalatestplus.play.examples.guice.mixedfixtures

import org.scalatestplus.play._
import play.api._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.DefaultActionBuilder

// #scalafunctionaltest-mixedfixtures
// MixedPlaySpec already mixes in MixedFixtures
class ExampleSpec extends MixedPlaySpec {

  // Some helper methods
  def buildApp[A](elems: (String, String)*): Application = {
    import play.api.http.MimeTypes._
    import play.api.mvc.Results._

    GuiceApplicationBuilder()
      .appRoutes(app => {
        case ("GET", "/testing") =>
          app.injector.instanceOf(classOf[DefaultActionBuilder]) {
            Ok("""
                 |<html>
                 | <head>
                 |   <title>Test Page</title>
                 |   <body>
                 |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
                 |   </body>
                 | </head>
                 |</html>
            """.stripMargin).as(HTML)
          }
      })
      .configure(Map(elems: _*))
      .build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  // If a test just needs an Application, use "new App":
  "The App function" must {
    "provide an Application" in new App(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new App(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
  }

  // If a test needs an Application and running TestServer, use "new Server":
  "The Server function" must {
    "provide an Application" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "send 404 on a bad request" in new Server {
      override def running() = {
        import java.net._
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
  }

  // If a test needs an Application, running TestServer, and Selenium
  // HtmlUnit driver use "new HtmlUnit":
  "The HtmlUnit function" must {
    "provide an Application" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "send 404 on a bad request" in new HtmlUnit {
      override def running() = {
        import java.net._
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
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

  // If a test needs an Application, running TestServer, and Selenium
  // Firefox driver use "new Firefox":
  "The Firefox function" must {
    "provide an application" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "send 404 on a bad request" in new Firefox {
      override def running() = {
        import java.net._
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
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

  // If a test needs an Application, running TestServer, and Selenium
  // Safari driver use "new Safari":
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "send 404 on a bad request" in new Safari {
      override def running() = {
        import java.net._
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
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

  // If a test needs an Application, running TestServer, and Selenium
  // Chrome driver use "new Chrome":
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "send 404 on a bad request" in new Chrome {
      override def running() = {
        import java.net._
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
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

  // If a test needs an Application, running TestServer, and Selenium
  // InternetExplorer driver use "new InternetExplorer":
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
      override def running() = getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "send 404 on a bad request" in new InternetExplorer {
      override def running() = {
        import java.net._
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
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

  // If a test does not need any special fixtures, just
  // write "in { () => ..."
  "Any old thing" must {
    "be doable without much boilerplate" in { () =>
      1 + 1 mustEqual 2
    }
  }
}
// #scalafunctionaltest-mixedfixtures

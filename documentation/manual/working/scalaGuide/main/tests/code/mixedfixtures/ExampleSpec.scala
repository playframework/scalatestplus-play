/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package org.scalatestplus.play.examples.mixedfixtures

import play.api.test._
import org.scalatestplus.play._
import play.api.{Play, Application}
import play.api.mvc._
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._

// #scalafunctionaltest-mixedfixtures
// MixedPlaySpec already mixes in MixedFixtures
class ExampleSpec extends MixedPlaySpec {

  // Some helper methods
  def buildApp[A](elems: (String, String)*) =
    new GuiceApplicationBuilder().configure(Map(elems:_*)).additionalRouter(Router.from {
      case GET(p"/testing") =>
        Action(
          Results.Ok(
            "<html>" +
              "<head><title>Test Page</title></head>" +
              "<body>" +
              "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
              "</body>" +
              "</html>"
          ).as("text/html")
        )
    }).build()
  
  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  // If a test just needs an Application, use "new App":
  "The App function" must {
    "provide an Application" in new App(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new App(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new App(buildApp("ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
  }

  // If a test needs an Application and running TestServer, use "new Server":
  "The Server function" must {
    "provide an Application" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new Server(buildApp("ehcacheplugin" -> "disabled")) {
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

  // If a test needs an Application, running TestServer, and Selenium
  // HtmlUnit driver use "new HtmlUnit":
  "The HtmlUnit function" must {
    "provide an Application" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new HtmlUnit(buildApp("ehcacheplugin" -> "disabled")) {
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

  // If a test needs an Application, running TestServer, and Selenium
  // Firefox driver use "new Firefox":
  "The Firefox function" must {
    "provide an pplication" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new Firefox(buildApp("ehcacheplugin" -> "disabled")) {
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

  // If a test needs an Application, running TestServer, and Selenium
  // Safari driver use "new Safari":
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new Safari(buildApp("ehcacheplugin" -> "disabled")) {
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

  // If a test needs an Application, running TestServer, and Selenium
  // Chrome driver use "new Chrome":
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new Chrome(buildApp("ehcacheplugin" -> "disabled")) {
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

  // If a test needs an Application, running TestServer, and Selenium
  // InternetExplorer driver use "new InternetExplorer":
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in new InternetExplorer(buildApp("ehcacheplugin" -> "disabled")) {
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

  // If a test does not need any special fixtures, just 
  // write "in { () => ..."
  "Any old thing" must {
    "be doable without much boilerplate" in { () =>
       1 + 1 mustEqual 2
     }
  }
}
// #scalafunctionaltest-mixedfixtures

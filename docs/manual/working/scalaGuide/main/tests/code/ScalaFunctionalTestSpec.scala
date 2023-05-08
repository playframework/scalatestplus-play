/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest

// #scalafunctionaltest-imports
import org.scalatest._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatestplus.play._
import play.api.http.MimeTypes
import play.api.test._
import play.api.test.Helpers._
// #scalafunctionaltest-imports

import play.api.mvc._

import play.api.test.Helpers.{ GET => GET_REQUEST }
import play.api.Application
import play.api.libs.ws._
import play.api.inject.guice._

abstract class MixedPlaySpec extends FixtureAnyWordSpec with Matchers with OptionValues with MixedFixtures

class ScalaFunctionalTestSpec extends MixedPlaySpec with Results {

  // lie and make this look like a DB model.
  case class Computer(name: String, introduced: Option[String])

  object Computer {
    def findById(id: Int): Option[Computer] = Some(Computer("Macintosh", Some("1984-01-24")))
  }

  "Scala Functional Test" should {

    // #scalafunctionaltest-application
    val application: Application = new GuiceApplicationBuilder()
      .configure("some.configuration" -> "value")
      .build()
    // #scalafunctionaltest-application

    val applicationWithRouter = GuiceApplicationBuilder()
      .appRoutes(app => {
        case ("GET", "/Bob") =>
          app.injector.instanceOf(classOf[DefaultActionBuilder]) {
            Ok("Hello Bob").as(MimeTypes.HTML)
          }
      })
      .build()

    // #scalafunctionaltest-respondtoroute
    "respond to the index Action" in new App(applicationWithRouter) {
      override def running() = {
        val Some(result) = route(app, FakeRequest(GET_REQUEST, "/Bob"))

        status(result) mustEqual OK
        contentType(result) mustEqual Some("text/html")
        contentAsString(result) must include("Hello Bob")
      }
    }
    // #scalafunctionaltest-respondtoroute

    // #scalafunctionaltest-testview
    "render index template" in new App {
      override def running() = {
        val html = views.html.index("Coco")

        contentAsString(html) must include("Hello Coco")
      }
    }
    // #scalafunctionaltest-testview

    // #scalafunctionaltest-testmodel
    val appWithMemoryDatabase = new GuiceApplicationBuilder().configure(inMemoryDatabase("test")).build()
    "run an application" in new App(appWithMemoryDatabase) {

      override def running() = {
        val Some(macintosh) = Computer.findById(21)

        macintosh.name mustEqual "Macintosh"
        macintosh.introduced.value mustEqual "1984-01-24"
      }
    }
    // #scalafunctionaltest-testmodel

    // #scalafunctionaltest-testwithbrowser
    def applicationWithBrowser: Application =
      GuiceApplicationBuilder()
        .appRoutes(app => {
          case ("GET", "/") =>
            app.injector.instanceOf(classOf[DefaultActionBuilder]) {
              Ok("""
                   |<html>
                   |<head><title>Hello Guest</title></head>
                   |<body>
                   |  <div id="title">Hello Guest, welcome to this website.</div>
                   |  <a href="/login">click me</a>
                   |</body>
                   |</html>
            """.stripMargin).as("text/html")
            }
          case ("GET", "/login") =>
            app.injector.instanceOf(classOf[DefaultActionBuilder]) {
              Ok("""
                   |<html>
                   |<head><title>Hello Coco</title></head>
                   |<body>
                   |  <div id="title">Hello Coco, welcome to this website.</div>
                   |</body>
                   |</html>
            """.stripMargin).as("text/html")
            }
        })
        .build()

    "run in a browser" in new HtmlUnit(appFun = { applicationWithBrowser }) {

      override def running() = {
        // Check the home page
        go to "http://localhost:" + port
        pageTitle mustEqual "Hello Guest"

        click.on(linkText("click me"))

        currentUrl mustEqual "http://localhost:" + port + "/login"
        pageTitle mustEqual "Hello Coco"
      }
    }
    // #scalafunctionaltest-testwithbrowser

    // #scalafunctionaltest-testpaymentgateway
    "test server logic" in new Server(appFun = { applicationWithBrowser }, port = 19001) {
      override def running() = {
        implicit val wsClient: WSClient = app.injector.instanceOf[WSClient]

        val myPublicAddress       = s"localhost:$port"
        val testPaymentGatewayURL = s"http://$myPublicAddress"
        // The test payment gateway requires a callback to this server before it returns a result...
        val callbackURL = s"http://$myPublicAddress/callback"

        // await is from play.api.test.FutureAwaits
        val response =
          await(wsClient.url(testPaymentGatewayURL).addQueryStringParameters("callbackURL" -> callbackURL).get())

        response.status mustEqual OK
      }
    }
    // #scalafunctionaltest-testpaymentgateway

    // #scalafunctionaltest-testws
    val appWithRoutes = GuiceApplicationBuilder()
      .appRoutes(app => {
        case ("GET", "/") =>
          app.injector.instanceOf(classOf[DefaultActionBuilder]) {
            Ok("ok")
          }
      })
      .build()

    "test WS logic" in new Server(appFun = appWithRoutes, port = 3333) {
      override def running() = {
        val wsClient = app.injector.instanceOf[WSClient]
        await(wsClient.url("http://localhost:3333").get()).status mustEqual OK
      }
    }
    // #scalafunctionaltest-testws
  }
}

/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest

import org.scalatest._
import org.scalatestplus.play._

import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET => GET_REQUEST, _}
import play.api.{GlobalSettings, Application}
import play.api.libs.ws._
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._

abstract class MixedPlaySpec extends fixture.WordSpec with MustMatchers with OptionValues with MixedFixtures

/**
 *
 */
class ScalaFunctionalTestSpec extends MixedPlaySpec with Results {

  // lie and make this look like a DB model.
  case class Computer(name: String, introduced: Option[String])

  object Computer {
    def findById(id: Int): Option[Computer] = Some(Computer("Macintosh", Some("1984-01-24")))
  }

  "Scala Functional Test" should {

    // #scalafunctionaltest-fakeApplication
    val applicationWithGlobal = new GuiceApplicationBuilder().global(new GlobalSettings() {
      override def onStart(app: Application) { println("Hello world!") }
    }).build()
    // #scalafunctionaltest-fakeApplication

    val application = new GuiceApplicationBuilder().additionalRouter(Router.from {
      case GET(p"/Bob") =>
        Action {
          Ok("Hello Bob") as "text/html; charset=utf-8"
        }
    }).build()


    // #scalafunctionaltest-respondtoroute
    "respond to the index Action" in new App(application) {
      val Some(result) = route(FakeRequest(GET_REQUEST, "/Bob"))

      status(result) mustEqual OK
      contentType(result) mustEqual Some("text/html")
      charset(result) mustEqual Some("utf-8")
      contentAsString(result) must include ("Hello Bob")
    }
    // #scalafunctionaltest-respondtoroute


    // #scalafunctionaltest-testview
    "render index template" in new App {
      val html = views.html.index("Coco")

      contentAsString(html) must include ("Hello Coco")
    }
    // #scalafunctionaltest-testview


    // #scalafunctionaltest-testmodel
    val appWithMemoryDatabase = new GuiceApplicationBuilder().configure(inMemoryDatabase("test")).build()
    "run an application" in new App(appWithMemoryDatabase) {

      val Some(macintosh) = Computer.findById(21)

      macintosh.name mustEqual "Macintosh"
      macintosh.introduced.value mustEqual "1984-01-24"
    }
    // #scalafunctionaltest-testmodel


    // #scalafunctionaltest-testwithbrowser
    def applicationWithBrowser = new GuiceApplicationBuilder().additionalRouter(Router.from {
      case GET(p"/") =>
        Action {
          Ok(
            """
              |<html>
              |<head><title>Hello Guest</title></head>
              |<body>
              |  <div id="title">Hello Guest, welcome to this website.</div>
              |  <a href="/login">click me</a>
              |</body>
              |</html>
            """.stripMargin) as "text/html"
        }
      case GET(p"/login") =>
        Action {
          Ok(
            """
              |<html>
              |<head><title>Hello Coco</title></head>
              |<body>
              |  <div id="title">Hello Coco, welcome to this website.</div>
              |</body>
              |</html>
            """.stripMargin) as "text/html"
        }
    }).build()

    "run in a browser" in new HtmlUnit(app = applicationWithBrowser) {

      // Check the home page
      go to "http://localhost:" + port
      pageTitle mustEqual "Hello Guest"

      click on linkText("click me")

      currentUrl mustEqual "http://localhost:" + port + "/login"
      pageTitle mustEqual "Hello Coco"
    }
    // #scalafunctionaltest-testwithbrowser

    // #scalafunctionaltest-testpaymentgateway
    "test server logic" in new Server(app = applicationWithBrowser, port = 19001) { port =>
      val myPublicAddress =  s"localhost:$port"
      val testPaymentGatewayURL = s"http://$myPublicAddress"
      // The test payment gateway requires a callback to this server before it returns a result...
      val callbackURL = s"http://$myPublicAddress/callback"

      // await is from play.api.test.FutureAwaits
      val response = await(WS.url(testPaymentGatewayURL).withQueryString("callbackURL" -> callbackURL).get())

      response.status mustEqual OK
    }
    // #scalafunctionaltest-testpaymentgateway

    // #scalafunctionaltest-testws
    val appWithRoutes = new GuiceApplicationBuilder().additionalRouter(Router.from{
      case GET(p"/") =>
        Action {
          Ok("ok")
        }
    }).build()

    "test WS logic" in new Server(app = appWithRoutes, port = 3333) {
      await(WS.url("http://localhost:3333").get()).status mustEqual OK
    }
    // #scalafunctionaltest-testws
  }
}

/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.playspec

import org.scalatest.concurrent.{ IntegrationPatience, ScalaFutures }
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc._

// #scalafunctionaltest-playspec
class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience {

  // Override app if you need an Application with other than
  // default parameters.
  override def fakeApplication(): Application = {

    import play.api.http.MimeTypes._
    import play.api.mvc.Results._

    GuiceApplicationBuilder()
      .appRoutes(app => {
        case ("GET", "/testing") => app.injector.instanceOf(classOf[DefaultActionBuilder]) {
          Ok(
            """
              |<html>
              | <head>
              |   <title>Test Page</title>
              |   <body>
              |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
              |   </body>
              | </head>
              |</html>""".stripMargin
          ).as(HTML)
        }
      }).build()
  }

  "WsScalaTestClient's" must {

    "wsUrl works correctly" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
      val futureResult = wsUrl("/testing").get
      val body = futureResult.futureValue.body
      val expectedBody =
        """
          |<html>
          | <head>
          |   <title>Test Page</title>
          |   <body>
          |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
          |   </body>
          | </head>
          |</html>""".stripMargin
      assert(body == expectedBody)
    }

    "wsCall works correctly" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
      val futureResult = wsCall(Call("get", "/testing")).get
      val body = futureResult.futureValue.body
      val expectedBody =
        """
          |<html>
          | <head>
          |   <title>Test Page</title>
          |   <body>
          |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
          |   </body>
          | </head>
          |</html>""".stripMargin
      assert(body == expectedBody)
    }
  }
}
// #scalafunctionaltest-playspec

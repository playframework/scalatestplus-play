/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.playspec

import org.scalatestplus.play._
import play.api.mvc._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._
import play.api.cache.ehcache.EhCacheModule
import play.api.libs.ws.WSClient

// #scalafunctionaltest-playspec
class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience {

  // Override app if you need an Application with other than
  // default parameters.
  override def fakeApplication() =
    new GuiceApplicationBuilder().disable[EhCacheModule].router(Router.from {
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

  "WsScalaTestClient's" must {

    "wsUrl works correctly" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
      val futureResult = wsUrl("/testing").get
      val body = futureResult.futureValue.body
      val expectedBody =
        "<html>" +
          "<head><title>Test Page</title></head>" +
          "<body>" +
          "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
          "</body>" +
          "</html>"
      assert(body == expectedBody)
    }

    "wsCall works correctly" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
      val futureResult = wsCall(Call("get", "/testing")).get
      val body = futureResult.futureValue.body
      val expectedBody =
        "<html>" +
          "<head><title>Test Page</title></head>" +
          "<body>" +
          "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
          "</body>" +
          "</html>"
      assert(body == expectedBody)
    }
  }
}
// #scalafunctionaltest-playspec

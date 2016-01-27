/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.playspec

import org.scalatestplus.play._
import play.api.mvc._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.IntegrationPatience
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._
import play.api.cache.EhCacheModule

// #scalafunctionaltest-playspec
class ExampleSpec extends PlaySpec with OneServerPerSuite with ScalaFutures with IntegrationPatience {

  // Override app if you need an Application with other than
  // default parameters.
  implicit override lazy val app =
    new GuiceApplicationBuilder().disable[EhCacheModule].additionalRouter(Router.from {
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

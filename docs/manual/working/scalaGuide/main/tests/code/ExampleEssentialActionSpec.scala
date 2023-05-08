/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest

import akka.stream.Materializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

// #scalatest-exampleessentialactionspec
class ExampleEssentialActionSpec extends PlaySpec with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer   = app.materializer
  implicit lazy val Action: DefaultActionBuilder = app.injector.instanceOf(classOf[DefaultActionBuilder])

  "An essential action" should {
    "can parse a JSON body" in {
      val action: EssentialAction = Action { request =>
        val value = (request.body.asJson.get \ "field").as[String]
        Ok(value)
      }

      val request = FakeRequest(POST, "/").withJsonBody(Json.parse("""{ "field": "value" }"""))

      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual "value"
    }
  }
}
// #scalatest-exampleessentialactionspec

/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest

// #scalatest-examplecontrollerspec
import scala.concurrent.Future

import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class ExampleControllerSpec extends PlaySpec with Results {

  "Example Page#index" should {
    "should be valid" in {
      val controller = new ExampleController(Helpers.stubControllerComponents())
      val result: Future[Result] = controller.index().apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "ok"
    }
  }
}
// #scalatest-examplecontrollerspec

// #scalatest-examplecontroller
class ExampleController(val controllerComponents: ControllerComponents) extends BaseController {
  def index() = Action {
    Ok("ok")
  }
}
// #scalatest-examplecontroller

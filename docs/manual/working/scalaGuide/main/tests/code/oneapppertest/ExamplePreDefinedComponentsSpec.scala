/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package oneapppertest

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerTestWithComponents
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, Helpers }

import scala.concurrent.Future

class ExamplePreDefinedComponentsSpec extends PlaySpec with OneAppPerTestWithComponents {

  // #scalacomponentstest-predefinedcomponents
  override def components: BuiltInComponents = new SomeAppComponents(context)
  // #scalacomponentstest-predefinedcomponents

  "The OneAppPerTestWithComponents trait" must {
    "provide an Application" in {
      import play.api.test.Helpers.{ GET, route }
      val Some(result): Option[Future[Result]] = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "define the db" in {
      app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
  }
}


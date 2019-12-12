/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package oneapppertest

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerTestWithComponents
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.test.Helpers

import scala.concurrent.Future

class ExamplePreDefinedOverrideComponentsSpec extends PlaySpec with OneAppPerTestWithComponents {

  // #scalacomponentstest-predefinedcomponentsoverride
  override def components: BuiltInComponents = new SomeAppComponents(context) {
    override lazy val configuration: Configuration =
      Configuration("ehcacheplugin" -> "enabled").withFallback(context.initialConfiguration)
  }

  // #scalacomponentstest-predefinedcomponentsoverride

  "The OneAppPerTestWithComponents trait" must {
    "provide an Application" in {
      import play.api.test.Helpers.GET
      import play.api.test.Helpers.route
      val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "override the configuration" in {
      app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("enabled")
    }
  }
}

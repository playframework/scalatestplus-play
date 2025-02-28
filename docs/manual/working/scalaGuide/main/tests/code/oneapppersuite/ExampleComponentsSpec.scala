/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest.oneapppersuite

import org.scalatest.ConfigMap
import org.scalatest.Outcome
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.test.Helpers

import scala.concurrent.Future

// #scalacomponentstest-oneapppersuite
class ExampleComponentsSpec extends PlaySpec with OneAppPerSuiteWithComponents {

  // #scalacomponentstest-inlinecomponents
  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird._

    lazy val router: Router = Router.from({ case GET(p"/") =>
      defaultActionBuilder {
        Results.Ok("success!")
      }
    })
    override lazy val configuration: Configuration =
      Configuration("foo" -> "bar", "ehcacheplugin" -> "disabled").withFallback(context.initialConfiguration)
  }
  // #scalacomponentstest-inlinecomponents

  "The OneAppPerSuiteWithComponents trait" must {
    "provide an Application" in {
      import play.api.test.Helpers.GET
      import play.api.test.Helpers.route
      val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "override the configuration" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
  }
}
// #scalacomponentstest-oneapppersuite

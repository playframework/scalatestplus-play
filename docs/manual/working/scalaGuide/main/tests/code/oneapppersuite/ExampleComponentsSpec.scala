/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.oneapppersuite

import org.scalatest.{ ConfigMap, Outcome }
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, Helpers }

import scala.concurrent.Future

// #scalacomponentstest-oneapppersuite
class ExampleComponentsSpec extends PlaySpec with OneAppPerSuiteWithComponents {

  // #scalacomponentstest-inlinecomponents
  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird._

    lazy val router: Router = Router.from({
      case GET(p"/") => defaultActionBuilder {
        Results.Ok("success!")
      }
    })
    override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("foo" -> "bar", "ehcacheplugin" -> "disabled")
  }
  // #scalacomponentstest-inlinecomponents

  "The OneAppPerSuiteWithComponents trait" must {
    "provide an Application" in {
      import play.api.test.Helpers.{ GET, route }
      val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "override the configuration" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
  }
}
// #scalacomponentstest-oneapppersuite


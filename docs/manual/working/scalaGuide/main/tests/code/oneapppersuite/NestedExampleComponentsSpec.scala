/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package oneapppersuite

import org.scalatest.{ DoNotDiscover, Suites, TestSuite }
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import org.scalatestplus.play.{ ConfiguredApp, PlaySpec }
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, Helpers }

import scala.concurrent.Future

// #scalacomponentstest-nestedsuites
class NestedExampleSpec extends Suites(
  new OneSpec,
  new TwoSpec,
  new RedSpec,
  new BlueSpec
) with OneAppPerSuiteWithComponents with TestSuite {

  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird._

    lazy val router: Router = Router.from({
      case GET(p"/") => defaultActionBuilder {
        Results.Ok("success!")
      }
    })

    override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("ehcacheplugin" -> "disabled")
  }
}

// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp {
  "OneSpec" must {
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)

      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
  }

}

@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredApp {

  "The NestedExampleSpec" must {
    "provide an Application" in {
      import play.api.test.Helpers.{ GET, route }
      val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
  }
}
// #scalacomponentstest-nestedsuites

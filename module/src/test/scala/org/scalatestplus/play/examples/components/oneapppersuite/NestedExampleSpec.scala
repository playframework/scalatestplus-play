/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package org.scalatestplus.play.examples.components.oneapppersuite

import org.scalatest.{ DoNotDiscover, Suites, TestSuite }
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import org.scalatestplus.play.{ ConfiguredApp, PlaySpec }
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, Helpers }

import scala.concurrent.Future

class NestedExampleSpec extends Suites(
  new OneSpec,
  new TwoSpec,
  new RedSpec,
  new BlueSpec
) with OneAppPerSuiteWithComponents with TestSuite {
  // Override fakeApplication if you need an Application with other than non-default parameters.
  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird._

    lazy val router: Router = Router.from({
      case GET(p"/") => defaultActionBuilder {
        Results.Ok("success!")
      }
    })

    override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("foo" -> "bar")
  }
}

// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp {
  "OneSpec" must {
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

      getConfig("foo") mustBe Some("bar")
    }
  }

}

@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredApp {

  "The NestedExampleSpec" must {
    "provide an Application" in {
      import play.api.test.Helpers.{ GET, route }
      val Some(result): Option[Future[Result]] = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
  }
}

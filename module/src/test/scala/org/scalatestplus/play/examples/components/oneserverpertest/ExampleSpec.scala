/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package org.scalatestplus.play.examples.components.oneserverpertest

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneServerPerTestWithComponents
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, Helpers }

import scala.concurrent.Future

class ExampleSpec extends PlaySpec with OneServerPerTestWithComponents {

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

  "The OneServerPerTestWithComponents trait" must {
    "provide an Application" in {
      import play.api.test.Helpers.{ GET, route }
      val Some(result): Option[Future[Result]] = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "override the configuration" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
  }
}


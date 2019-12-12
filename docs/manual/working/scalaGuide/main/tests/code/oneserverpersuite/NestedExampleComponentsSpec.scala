/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package oneserverpersuite

import org.scalatest.DoNotDiscover
import org.scalatest.Suites
import org.scalatest.TestSuite
import org.scalatestplus.play.components._
import org.scalatestplus.play.ConfiguredServer
import org.scalatestplus.play.PlaySpec
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.test.Helpers

import scala.concurrent.Future

class NestedExampleSpec
    extends Suites(new OneSpec, new TwoSpec, new RedSpec, new BlueSpec)
    with OneServerPerSuiteWithComponents
    with TestSuite {
  // Override fakeApplication if you need an Application with other than non-default parameters.
  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird._

    lazy val router: Router = Router.from({
      case GET(p"/") =>
        defaultActionBuilder {
          Results.Ok("success!")
        }
    })

    override lazy val configuration: Configuration =
      Configuration("ehcacheplugin" -> "disabled").withFallback(context.initialConfiguration)

  }
}

// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer {
  "OneSpec" must {
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)

      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
  }

}

@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredServer

@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredServer

@DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredServer {

  "The NestedExampleSpeccc" must {
    "provide an Application" in {
      import play.api.test.Helpers.GET
      import play.api.test.Helpers.route
      val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "provide an actual running server" in {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boum")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
}

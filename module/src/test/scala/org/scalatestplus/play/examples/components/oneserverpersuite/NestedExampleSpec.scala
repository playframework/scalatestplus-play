/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalatestplus.play.examples.components.oneserverpersuite

import org.scalatest.DoNotDiscover
import org.scalatest.Suites
import org.scalatest.TestSuite
import org.scalatestplus.play.components.*
import org.scalatestplus.play.ConfiguredServer
import org.scalatestplus.play.PlaySpec
import play.api.*
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.FakeRequest
import play.api.test.Helpers

import scala.concurrent.Future

class NestedExampleSpec
    extends Suites(new OneSpec, new TwoSpec, new RedSpec, new BlueSpec)
    with OneServerPerSuiteWithComponents
    with TestSuite {

  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird.*

    lazy val router: Router = Router.from { case GET(p"/") =>
      defaultActionBuilder {
        Results.Ok("success!")
      }
    }

    override lazy val configuration: Configuration =
      Configuration("foo" -> "bar").withFallback(context.initialConfiguration)

  }
}

// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer {
  "OneSpec" must {
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)

      getConfig("foo") mustBe Some("bar")
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
      import java.net.*
      val url = new URI("http://localhost:" + port + "/boum").toURL
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
}

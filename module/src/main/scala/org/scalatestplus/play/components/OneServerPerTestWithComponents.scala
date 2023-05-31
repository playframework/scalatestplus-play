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

package org.scalatestplus.play.components

import org.scalatest.Suite
import org.scalatestplus.play.BaseOneServerPerTest
import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application

/**
 * An extension of [[BaseOneServerPerTest]] providing Compile-time DI.
 *
 * Trait that provides a new `Application` and running `TestServer` instance for each test executed in a ScalaTest `Suite`
 *
 * This `SuiteMixin` trait overrides ScalaTest's `withFixture` method to create a new `Application` and `TestServer`
 * before each test, and ensure they are cleaned up after the test has completed. The `Application` is available (implicitly) from
 * method `app`. The `TestServer`'s port number is available as `port` (and implicitly available as `portNumber`, wrapped
 * in a [[org.scalatestplus.play.PortNumber PortNumber]]).
 *
 * By default, this trait creates a new `Application` for each test according to the components defined in the test.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * import org.scalatestplus.play.PlaySpec
 * import org.scalatestplus.play.components.OneServerPerTestWithComponents
 * import play.api._
 * import play.api.mvc.Result
 * import play.api.test.Helpers._
 * import play.api.test.{FakeRequest, Helpers}
 *
 * import scala.concurrent.Future
 *
 * class ExampleComponentsSpec extends PlaySpec with OneServerPerTestWithComponents {
 *
 *   override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {
 *
 *     import play.api.mvc.Results
 *     import play.api.routing.Router
 *     import play.api.routing.sird._
 *
 *     lazy val router: Router = Router.from({
 *       case GET(p"/") => defaultActionBuilder {
 *         Results.Ok("success!")
 *       }
 *     })
 *
 *     override lazy val configuration: Configuration = Configuration("foo" -> "bar", "ehcacheplugin" -> "disabled").withFallback(context.initialConfiguration)
 *   }
 *
 *   "The OneServerPerTestWithComponents trait" must {
 *     "provide an Application" in {
 *       import play.api.test.Helpers.{GET, route}
 *       val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
 *       Helpers.contentAsString(result) must be("success!")
 *     }
 *     "override the configuration" in {
 *       app.configuration.getOptional[String]("foo") mustBe Some("bar")
 *     }
 *   }
 * }
 * </pre>
 */
trait OneServerPerTestWithComponents
    extends BaseOneServerPerTest
    with WithApplicationComponents
    with FakeApplicationFactory {
  this: Suite =>

  override def fakeApplication(): Application = newApplication
}

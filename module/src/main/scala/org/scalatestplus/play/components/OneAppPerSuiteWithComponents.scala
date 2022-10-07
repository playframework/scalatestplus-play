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

import org.scalatest.TestSuite
import org.scalatestplus.play.BaseOneAppPerSuite
import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application

/**
 * An extension of [[BaseOneAppPerSuite]] providing Compile-time DI.
 *
 * Provides a new `Application` instance per ScalaTest `Suite`.
 *
 * By default, this trait creates a new `Application` for the `Suite` according to the components defined in the test.
 *
 * This `TestSuiteMixin` trait's overridden `run` method calls `Play.start`, passing in the
 * `Application` provided by `app`, before executing the `Suite` via a call to `super.run`.
 * In addition, it places a reference to the `Application` provided by `app` into the `ConfigMap`
 * under the key `org.scalatestplus.play.app`.  This allows any nested `Suite`s to access the `Suite`'s
 * `Application` as well, most easily by having the nested `Suite`s mix in the
 * [[org.scalatestplus.play.ConfiguredApp ConfiguredApp]] trait.  On the status returned by `super.run`, this
 * trait's overridden `run` method registers a call to `Play.stop` to be executed when the `Status`
 * completes, and returns the same `Status`. This ensure the `Application` will continue to execute until
 * all nested suites have completed, after which the `Application` will be stopped.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * import org.scalatestplus.play.PlaySpec
 * import org.scalatestplus.play.components.OneServerPerSuiteWithComponents
 * import play.api._
 * import play.api.mvc.Result
 * import play.api.test.Helpers._
 * import play.api.test.{FakeRequest, Helpers}
 *
 * import scala.concurrent.Future
 *
 * class ExampleComponentsSpec extends PlaySpec with OneServerPerSuiteWithComponents {
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
 *   "The OneServerPerSuiteWithComponents trait" must {
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
 *
 * If you have many tests that can share the same `Application`, and you don't want to put them all into one
 * test class, you can place them into different `Suite` classes.
 * These will be your nested suites. Create a master suite that extends `OneAppPerSuiteWithComponents` and declares the nested
 * `Suite`s. Annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredApp`. Here's an example:
 *
 * <pre class="stHighlight">
 * import org.scalatest.{DoNotDiscover, Suites, TestSuite}
 * import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
 * import org.scalatestplus.play.{ConfiguredApp, PlaySpec}
 * import play.api._
 * import play.api.mvc.Result
 * import play.api.test.Helpers._
 * import play.api.test.{FakeRequest, Helpers}
 *
 * import scala.concurrent.Future
 *
 *
 * class NestedExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with OneAppPerSuiteWithComponents with TestSuite {
 *   // Override fakeApplication if you need an Application with other than non-default parameters.
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
 *     override lazy val configuration: Configuration = Configuration("ehcacheplugin" -> "disabled").withFallback(context.initialConfiguration)
 *
 *   }
 * }
 *
 * // These are the nested suites
 * @DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp {
 *   "OneSpec" must {
 *     "make the Application available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
 *
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *   }
 *
 * }
 *
 * @DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp
 *
 * @DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp
 *
 * @DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredApp {
 *
 *   "The NestedExampleSpec" must {
 *     "provide an Application" in {
 *       import play.api.test.Helpers.{ GET, route }
 *       val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
 *       Helpers.contentAsString(result) must be("success!")
 *     }
 *   }
 * }
 * </pre>
 */
trait OneAppPerSuiteWithComponents
    extends BaseOneAppPerSuite
    with WithApplicationComponents
    with FakeApplicationFactory {
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

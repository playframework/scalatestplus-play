package org.scalatestplus.play.components

import org.scalatest.{ TestSuite, TestSuiteMixin }
import org.scalatestplus.play.{ BaseOneAppPerTest, FakeApplicationFactory }
import play.api.Application

/**
 * An extension of [[BaseOneAppPerTest]] providing Compile-time DI.
 *
 * Trait that provides a new `Application` instance for each test.
 *
 * This `TestSuiteMixin` trait's overridden `withFixture` method creates a new `Application`
 * before each test and ensures it is cleaned up after the test has completed. You can
 * access the `Application` from your tests as method `app` (which is marked implicit).
 *
 * By default, this trait creates a new `Application` for each test according to the components defined in the test.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * import org.scalatestplus.play.PlaySpec
 * import org.scalatestplus.play.components.OneAppPerTestWithComponents
 * import play.api._
 * import play.api.mvc.Result
 * import play.api.test.Helpers._
 * import play.api.test.{FakeRequest, Helpers}
 *
 * import scala.concurrent.Future
 *
 * class ExampleComponentsSpec extends PlaySpec with OneAppPerTestWithComponents {
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
 *     override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("foo" -> "bar", "ehcacheplugin" -> "disabled")
 *   }
 *
 *   "The OneAppPerTestWithComponents trait" must {
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
trait OneAppPerTestWithComponents
  extends BaseOneAppPerTest
  with WithApplicationComponents
  with FakeApplicationFactory with TestSuiteMixin {
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

package org.scalatestplus.play.components

import org.scalatest.TestSuite
import org.scalatestplus.play.BaseOneServerPerTest
import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application

/**
 * An extension of [[BaseOneServerPerTest]] providing Compile-time DI.
 *
 * Trait that provides a new `Application` and running `TestServer` instance for each test executed in a ScalaTest `Suite`
 *
 * This `TestSuiteMixin` trait overrides ScalaTest's `withFixture` method to create a new `Application` and `TestServer`
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
 *     override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("foo" -> "bar", "ehcacheplugin" -> "disabled")
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
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

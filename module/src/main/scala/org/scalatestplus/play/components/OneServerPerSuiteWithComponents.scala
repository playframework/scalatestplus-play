package org.scalatestplus.play.components

import org.scalatest.Suite
import org.scalatestplus.play.BaseOneServerPerSuite
import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application

/**
 * An extension of [[BaseOneServerPerSuite]] providing Compile-time DI.
 *
 * Trait that provides a new `Application` and running `TestServer` instance per ScalaTest `Suite`.
 *
 * By default, this trait creates a new `Application` for the `Suite` according to the components defined in the test, this
 * is made available via the `app` field defined in this trait and a new `TestServer` for the `Suite` using the port number provided by
 * its `port` field and the `Application` provided by its `app` field. If your `Suite` needs a different port number,
 * override `port`.
 *
 * This `SuiteMixin` trait's overridden `run` method calls `start` on the `TestServer`
 * before executing the `Suite` via a call to `super.run`.
 * In addition, it places a reference to the `Application` provided by `app` into the `ConfigMap`
 * under the key `org.scalatestplus.play.app` and to the port number provided by `port` under the key
 * `org.scalatestplus.play.port`.  This allows any nested `Suite`s to access the `Suite`'s
 * `Application` and port number as well, most easily by having the nested `Suite`s mix in the
 * [[org.scalatestplus.play.ConfiguredServer ConfiguredServer]] trait. On the status returned by `super.run`, this
 * trait's overridden `run` method registers a call to `stop` on the `TestServer` to be executed when the `Status`
 * completes, and returns the same `Status`. This ensure the `TestServer` will continue to execute until
 * all nested suites have completed, after which the `TestServer` will be stopped.
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
 * If you have many tests that can share the same `Application` and `TestServer`, and you don't want to put them all into one
 * test class, you can place them into different `Suite` classes.
 * These will be your nested suites. Create a master suite that extends `OneServerPerSuite` and declares the nested
 * `Suite`s. Annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredServer`. Here's an example:
 *
 * <pre class="stHighlight">
 * import org.scalatest.{ DoNotDiscover, Suites, Suite }
 * import org.scalatestplus.play.components._
 * import org.scalatestplus.play.{ ConfiguredServer, PlaySpec }
 * import play.api._
 * import play.api.mvc.Result
 * import play.api.test.Helpers._
 * import play.api.test.{ FakeRequest, Helpers }
 *
 * import scala.concurrent.Future
 *
 * class NestedExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with OneServerPerSuiteWithComponents with Suite {
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
 * @DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer {
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
 * @DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredServer
 *
 * @DoNotDiscover class RedSpec extends PlaySpec with ConfiguredServer
 *
 * @DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredServer {
 *
 *   "The NestedExampleSpeccc" must {
 *     "provide an Application" in {
 *       import play.api.test.Helpers.{ GET, route }
 *       val Some(result: Future[Result]) = route(app, FakeRequest(GET, "/"))
 *       Helpers.contentAsString(result) must be("success!")
 *     }
 *     "provide an actual running server" in {
 *       import java.net._
 *       val url = new URL("http://localhost:" + port + "/boum")
 *       val con = url.openConnection().asInstanceOf[HttpURLConnection]
 *       try con.getResponseCode mustBe 404
 *       finally con.disconnect()
 *     }
 *   }
 * }
 * </pre>
 */
trait OneServerPerSuiteWithComponents
    extends BaseOneServerPerSuite
    with WithApplicationComponents
    with FakeApplicationFactory {
  this: Suite =>

  override def fakeApplication(): Application = newApplication
}

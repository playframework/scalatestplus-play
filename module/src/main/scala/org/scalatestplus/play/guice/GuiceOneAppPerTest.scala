package org.scalatestplus.play.guice

import org.scalatest.Suite
import org.scalatestplus.play.BaseOneAppPerTest

/**
 * Trait that provides a new `Application` instance for each test.
 *
 * This `SuiteMixin` trait's overridden `withFixture` method creates a new `Application`
 * before each test and ensures it is cleaned up after the test has completed. You can
 * access the `Application` from your tests as method `app` (which is marked implicit).
 *
 * By default, this trait creates a new `Application` for each test using default parameter values, which
 * is returned by the `newAppForTest` method defined in this trait. If your tests need a `Application` with non-default
 * parameters, override `newAppForTest` to return it.
 *
 * Here's an example that demonstrates some of the services provided by this trait:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneapppertest
 *
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import org.scalatestplus.play.guice.GuiceOneAppPerTest
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * class ExampleSpec extends PlaySpec with GuiceOneAppPerTest {
 *
 *   // Override newAppForTest if you need an Application with other than non-default parameters.
 *   implicit override def newAppForTest(testData: TestData): Application =
 *     new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
 *
 *   "The OneAppPerTest trait" must {
 *     "provide an Application" in {
 *       app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the Application available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *   }
 * }
 * </pre>
 */
trait GuiceOneAppPerTest extends BaseOneAppPerTest with GuiceFakeApplicationFactory { this: Suite =>

}

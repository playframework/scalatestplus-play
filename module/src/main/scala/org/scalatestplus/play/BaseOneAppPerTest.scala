package org.scalatestplus.play

import org.scalatest._
import play.api.Application
import play.api.Play
import play.api.test.Helpers

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
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * class ExampleSpec extends PlaySpec with OneAppPerTest {
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
trait BaseOneAppPerTest extends SuiteMixin with BeforeAndAfterEachTestData with AppProvider {
  this: Suite with FakeApplicationFactory =>

  private var appPerTest: Application = _

  /**
   * Creates new instance of `Application` with parameters set to their defaults. Override this method if you
   * need a `Application` created with non-default parameter values.
   */
  def newAppForTest(testData: TestData): Application = fakeApplication()

  /**
   * Implicit method that returns the `Application` instance for the current test.
   */
  final implicit def app: Application = synchronized { appPerTest }

  protected override def beforeEach(td: TestData): Unit = {
    synchronized { appPerTest = newAppForTest(td) }
    Play.start(appPerTest)
    super.beforeEach(td)
  }

  protected override def afterEach(td: TestData): Unit = {
    try {
      super.afterEach(td)
    } finally {
      Play.stop(appPerTest)
    }
  }

}

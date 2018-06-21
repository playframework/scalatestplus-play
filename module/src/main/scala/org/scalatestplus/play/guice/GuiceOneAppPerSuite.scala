/*
 * Copyright 2001-2016 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatestplus.play.guice

import org.scalatest.TestSuite
import org.scalatestplus.play.BaseOneAppPerSuite

/**
 * Trait that provides a new `Application` instance per ScalaTest `Suite`.
 *
 * By default, this trait creates a new `Application` for the `Suite` using default parameter values, which
 * is made available via the `app` field defined in this trait. If your `Suite` needs a `Application` with non-default
 * parameters, override `app` to create it the way you need it.
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
 * package org.scalatestplus.play.examples.oneapppersuite
 *
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * class ExampleSpec extends PlaySpec with GuiceOneAppPerSuite {
 *
 *   // Override app if you need an Application with other than non-default parameters.
 *   def fakeApplication(): Application =
 *     new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
 *
 *   "The GuiceOneAppPerSuite trait" must {
 *     "provide a FakeApplication" in {
 *       app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *   }
 * }
 * </pre>
 *
 * If you have many tests that can share the same `Application`, and you don't want to put them all into one
 * test class, you can place them into different `Suite` classes.
 * These will be your nested suites. Create a master suite that extends `GuiceOneAppPerSuite` and declares the nested
 * `Suite`s. Annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredApp`. Here's an example:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneapppersuite
 *
 * import play.api.test._
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 * import play.api.inject.guice._
 *
 * // This is the "master" suite
 * class NestedExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with GuiceOneAppPerSuite {
 *   // Override app if you need an Application with other than non-default parameters.
 *   def fakeApplication(): Application =
 *     new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
 * }
 *
 * // These are the nested suites
 * @DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp
 * @DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp
 * @DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp
 *
 * @DoNotDiscover
 * class BlueSpec extends PlaySpec with ConfiguredApp {
 *
 *   "The GuiceOneAppPerSuite trait" must {
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
trait GuiceOneAppPerSuite extends BaseOneAppPerSuite with GuiceFakeApplicationFactory { this: TestSuite =>

}

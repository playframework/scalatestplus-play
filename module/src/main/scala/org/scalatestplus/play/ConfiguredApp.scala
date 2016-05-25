/*
 * Copyright 2001-2014 Artima, Inc.
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
package org.scalatestplus.play

import play.api.test._
import org.scalatest._
import play.api.{Application, Play}

/**
 * Trait that provides a configured `Application` to the suite into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.GuiceOneAppPerSuite GuiceOneAppPerSuite]]
 * to make use of the `Application` provided by `GuiceOneAppPerSuite`. Trait `GuiceOneAppPerSuite` will ensure
 * the `Application` is placed in the `ConfigMap` under the key `org.scalatestplus.play.app` before
 * nested suites are invoked. This represents the "configured application" that is passed from the enclosing
 * suite to the nested suites. Trait `ConfiguredApp` extracts the `Application` from the `ConfigMap`
 * and makes it available via the `app` method it provides.
 *
 * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`. Here's an example,
 * taken from `GuiceOneAppPerSuite`'s documentation:
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
 *       app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "start the FakeApplication" in {
 *       Play.maybeApplication mustBe Some(app)
 *     }
 *   }
 * }
 * </pre>
 */
trait ConfiguredApp extends SuiteMixin { this: Suite => 

  private var configuredApp: Application = _

  /**
   * The "configured" `Application` instance that was passed into `run` via the `ConfigMap`.
   *
   * @return the configured `Application`
   */
  implicit final def app: Application = synchronized { configuredApp }

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.app" whose value is a `Application`,
   * and if it exists, sets it as the `Application` that will be returned from the `app` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.app" in `args.configMap`, or the associated value is
   * not a `Application`, throws `IllegalArgumentException`.
   *
   * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws IllegalArgumentException if the `Application` does not appear in `args.configMap` under the expected key
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[Application]("org.scalatestplus.play.app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new IllegalArgumentException("ConfiguredApp needs an Application value associated with key \"org.scalatestplus.play.app\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


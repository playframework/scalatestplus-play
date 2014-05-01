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
import play.api.Play

/**
 * Trait that provides a configured `FakeApplication` to the suite into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.OneAppPerSuite OneAppPerSuite]]
 * to make use of the `FakeApplication` provided by `OneAppPerSuite`. Trait `OneAppPerSuite` will ensure
 * the `FakeApplication` is placed in the `ConfigMap` under the key `org.scalatestplus.play.app` before
 * nested suites are invoked. This represents the "configured application" that is passed from the enclosing
 * suite to the nested suites. Trait `ConfiguredApp` extracts the `FakeApplication` from the `ConfigMap`
 * and makes it available via the `app` method it provides.
 *
 * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`. Here's an example,
 * taken from `OneAppPerSuite`'s documentation:
 *
 * <pre class="stHighlight">
 * package org.scalatestplus.play.examples.oneapppersuite
 *  
 * import play.api.test._
 * import org.scalatest._
 * import org.scalatestplus.play._
 * import play.api.{Play, Application}
 *  
 * // This is the "master" suite
 * class NestedExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with OneAppPerSuite {
 *   // Override app if you need a FakeApplication with other than non-default parameters.
 *   implicit override lazy val app: FakeApplication =
 *     FakeApplication(additionalConfiguration = Map("ehcacheplugin" -> "disabled"))
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
 *   "The OneAppPerSuite trait" must {
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

  private var configuredApp: FakeApplication = _

  /**
   * The "configured" `FakeApplication` instance that was passed into `run` via the `ConfigMap`.
   *
   * @return the configured `FakeApplication`
   */
  implicit final def app: FakeApplication = synchronized { configuredApp }

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.app" whose value is a `FakeApplication`, 
   * and if it exists, sets it as the `FakeApplication` that will be returned from the `app` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.app" in `args.configMap`, or the associated value is
   * not a `FakeApplication`, throws `IllegalArgumentException`.
   *
   * To prevent discovery of nested suites you can annotate them with `@DoNotDiscover`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws IllegalArgumentException if the `FakeApplication` does not appear in `args.configMap` under the expected key
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[FakeApplication]("org.scalatestplus.play.app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new IllegalArgumentException("ConfiguredApp needs a FakeApplication value associated with key \"org.scalatestplus.play.app\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


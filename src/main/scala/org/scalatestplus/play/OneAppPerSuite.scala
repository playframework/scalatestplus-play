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
 * Trait that provides a new `FakeApplication` instance per ScalaTest `Suite`.
 *
 * By default, this trait creates a new `FakeApplication` for the `Suite` using default parameter values, which
 * is made available via the `app` field defined in this trait. If your `Suite` needs a `FakeApplication` with non-default 
 * parameters, override `app` to create it.
 *
 * This `SuiteMixin` trait's overridden `run` method calls `Play.start`, passing in the
 * `FakeApplication` provided by `app`, before executing the `Suite` via a call to `super.run`.
 * In addition, it places a reference to the `FakeApplication` provided by `app` into the `ConfigMap`
 * under the key `org.scalatestplus.play.app`.  This allows any nested `Suite`s to access the `Suite`'s 
 * `FakeApplication` as well, most easily by having the nested `Suite`s mix in the
 * [[org.scalatestplus.play.ConfiguredApp ConfiguredApp]] trait.  Once `super.run` completes, this
 * trait's overriden `run` method calls `Play.stop`.
 *
 * If you have many tests that can share the same `FakeApplication`, and you don't want to put them all into one
 * test class, you can place them into different `Suite` classes.
 * These will be your nested suites. Create a master suite that extends `OneAppPerSuite` and declares the nested 
 * `Suite`s. Annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredApp`. Here's an example:
 *
 * <pre class="stHighlight">
 * import org.scalatest._
 * import org.scalatestplus.play._
 *
 * // You can organize your tests that can share the same FakeApplication
 * // into different Suite classes that extend ConfiguredApp
 * // and are annotated with @DoNotDiscover:
 * @DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp
 * @DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp
 * @DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp
 * @DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredApp
 *
 * // Then declare them as nested Suites in a "master" Suite that
 * // extends OneAppPerSuite:
 * class OneAppPerSuiteExampleSpec extends Suites(
 *   new OneSpec,
 *   new TwoSpec,
 *   new RedSpec,
 *   new BlueSpec
 * ) with OneAppPerSuite
 * </pre>
 */
trait OneAppPerSuite extends SuiteMixin { this: Suite => 

  /**
   * An implicit instance of `FakeApplication`.
   *
   * This trait's implementation initializes this `lazy` `val` with a new instance of `FakeApplication` with
   * parameters set to their defaults. Override this `lazy` `val` if you need a `FakeApplication` created with non-default parameter values.
   */
  implicit lazy val app: FakeApplication = new FakeApplication()
  
  /**
   * Invokes `Play.start`, passing in the `FakeApplication` provided by `app`, and places
   * that same `FakeApplication` into the `ConfigMap` under the key `"org.scalatestplus.play.app"` to make it available
   * to nested suites; calls `super.run`; and finally calls `Play.stop`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    try {
      Play.start(app)
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app)
      val newArgs = args.copy(configMap = newConfigMap)
      super.run(testName, newArgs)
    } finally Play.stop()
  }
}   


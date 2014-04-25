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
 * Trait that provides one `FakeApplication` instance per ScalaTest `Suite`.
 * 
 * <p>
 * It overrides ScalaTest's `Suite.run` method to call `Play.start()` before, 
 * and `Play.stop()` after, executing the tests. In the suite that mixes in `OneAppPerSuite`,
 * you can access the `FakeApplication` using the `app` field. In nested suites,
 * you can access the `FakeApplication` from the `args.configMap`, where it is associated
 * with key `"org.scalatestplus.play.app"`.
 * </p>
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
   * Overriden to start `Play` before running the tests, pass a `FakeApplication` into the tests in 
   * `args.configMap` via "org.scalatestplus.play.app" key, call `super.run` and stop `Play` after test executions.
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


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
 */
trait ConfiguredApp extends SuiteMixin { this: Suite => 

  private var configuredApp: FakeApplication = _

  /**
   * Method that provides the configured `FakeApplication`, which is set by the overridden `run` method.
   *
   * @return the configured `FakeApplication`
   */
  implicit final def app: FakeApplication = synchronized { configuredApp }

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.app" whose value is a `FakeApplication`, 
   * and if it exists, sets it as the `FakeApplication` that will be returned from `app` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.app" in `args.configMap`, or the associated value is
   * not a `FakeApplication`, throws `IllegalArgumentException`.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws IllegalArgumentException no `FakeApplication` or object passed in as "org.scalatestplus.play.app" in `args.configMap` is not a `FakeApplication`
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[FakeApplication]("org.scalatestplus.play.app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new IllegalArgumentException("ConfiguredApp needs a FakeApplication value associated with key \"org.scalatestplus.play.app\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


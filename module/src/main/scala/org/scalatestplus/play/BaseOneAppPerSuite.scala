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

import org.scalatest.{Args, Status, Suite, SuiteMixin}
import play.api.{Application, Play}

/**
 * The base abstract trait for one app per suite.
 */
trait BaseOneAppPerSuite extends SuiteMixin { this: Suite with FakeApplicationFactory =>

  /**
   * An implicit instance of `Application`.
   */
  implicit lazy val app: Application = fakeApplication()

  /**
   * Invokes `Play.start`, passing in the `Application` provided by `app`, and places
   * that same `Application` into the `ConfigMap` under the key `org.scalatestplus.play.app` to make it available
   * to nested suites; calls `super.run`; and lastly ensures `Play.stop` is invoked after all tests and nested suites have completed.
   *
   * @param testName an optional name of one test to run. If `None`, all relevant tests should be run.
   *                 I.e., `None` acts like a wildcard that means run all relevant tests in this `Suite`.
   * @param args the `Args` for this run
   * @return a `Status` object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    Play.start(app)
    try {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app)
      val newArgs = args.copy(configMap = newConfigMap)
      val status = super.run(testName, newArgs)
      status.whenCompleted { _ => Play.stop(app) }
      status
    }
    catch { // In case the suite aborts, ensure the app is stopped
      case ex: Throwable =>
        Play.stop(app)
        throw ex
    }
  }
}

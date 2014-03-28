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
 * Trait that provides one <code>FakeApplication</code> instance per ScalaTest <code>Suite</code>.
 * 
 * <p>
 * It overrides ScalaTest's <code>Suite.run</code> method to call <code>Play.start()</code> before, 
 * and <code>Play.stop()</code> after, executing the tests. In the suite that mixes in <code>OneAppPerSuite</code>,
 * you can access the <code>FakeApplication</code> using the <code>app</code> field. In nested suites,
 * you can access the <code>FakeApplication</code> from the <code>args.configMap</code>, where it is associated
 * with key <code>"org.scalatestplus.play.app"</code>.
 * </p>
 */
trait OneAppPerSuite extends SuiteMixin { this: Suite => 

  /**
   * An implicit instance of <code>FakeApplication</code>.
   */
  implicit val app: FakeApplication = new FakeApplication()
  
  /**
   * Overriden to start <code>Play</code> before running the tests, pass a <code>FakeApplication</code> into the tests in 
   * <code>args.configMap</code> via "org.scalatestplus.play.app" key, call <code>super.run</code> and stop <code>Play</code> after test executions.
   *
   * @param testName an optional name of one test to run. If <code>None</code>, all relevant tests should be run.
   *                 I.e., <code>None</code> acts like a wildcard that means run all relevant tests in this <code>Suite</code>.
   * @param args the <code>Args</code> for this run
   * @return a <code>Status</code> object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
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


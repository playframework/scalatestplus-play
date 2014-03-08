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
 * Trait that provides a configured <code>FakeApplication</code> to the running suite.
 */
trait ConfiguredApp extends SuiteMixin { this: Suite => 

  private var configuredApp: FakeApplication = _

  /**
   * Method that provides the instance of configured <code>FakeApplication</code>
   *
   * @return the instance of configured <code>FakeApplication</code>
   */
  implicit final def app: FakeApplication = synchronized { configuredApp }

  /**
   * Overriden run method that read the <code>FakeApplication</code> passed in from <code>args.configMap</code> as "app", 
   * and set it as the instance of <code>FakeApplication</code> that will be returned from <code>app</code> method.  It 
   * then proceed to call <code>super.run</code>.
   *
   * If no <code>FakeApplication</code> or object passed in as "app" in <code>args.configMap</code> is not a <code>FakeApplication</code>, 
   * <code>IllegalArgumentException</code> will be thrown.
   *
   * @param testName an optional name of one test to run. If <code>None</code>, all relevant tests should be run.
   *                 I.e., <code>None</code> acts like a wildcard that means run all relevant tests in this <code>Suite</code>.
   * @param args the <code>Args</code> for this run
   * @return a <code>Status</code> object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   *         
   * @throws IllegalArgumentException no <code>FakeApplication</code> or object passed in as "app" in <code>args.configMap</code> is not a <code>FakeApplication</code>
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[FakeApplication]("app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new IllegalArgumentException("ConfiguredApp needs a FakeApplication value associated with key \"app\" in the config map")
    }
    super.run(testName, args)
  }
}   


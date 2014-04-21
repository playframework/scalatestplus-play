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

/**
 * Trait that provides one <code>TestServer</code> and <code>FakeApplication</code> instance per ScalaTest <code>Suite</code>.
 * 
 * It overrides ScalaTest's <code>Suite.run</code> method to start a <code>TestServer</code> before test execution, 
 * and stop the <code>TestServer</code> automatically after test execution has completed. 
 * In the suite that mixes in <code>OneServerPerSuite</code>,
 * you can access the <code>FakeApplication</code> via the <code>app</code> field and the port used by the <code>TestServer</code>
 * via the <code>port</code>field. In nested suites,
 * you can access the <code>FakeApplication</code> and port number from the <code>args.configMap</code>, where they are associated
 * with keys <code>"org.scalatestplus.play.app"</code> and <code>"org.scalatestplus.play.port"</code>, respectively.
 */
trait OneServerPerSuite extends SuiteMixin { this: Suite =>

  /**
   * An implicit instance of <code>FakeApplication</code>.
   */
  implicit val app: FakeApplication = new FakeApplication()

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  val port: Int = Helpers.testServerPort

  /**
   * Implicit <code>PortNumber</code> instance that wraps <code>port</code>. The value returned from <code>portNumber.value</code>
   * will be same as the value of <code>port</code>.
   */
  implicit lazy val portNumber: PortNumber = PortNumber(port)

  /**
   * Overriden to start <code>TestServer</code> before running the tests, pass a <code>FakeApplication</code> into the tests in 
   * <code>args.configMap</code> via "org.scalatestplus.play.app" key and port used by the started <code>TestServer</code> via the "org.scalatestplus.play.port" key.  It then calls 
   * <code>super.run</code> to execute the tests and stop <code>TestServer</code> automatically after test executions.
   *
   * @param testName an optional name of one test to run. If <code>None</code>, all relevant tests should be run.
   *                 I.e., <code>None</code> acts like a wildcard that means run all relevant tests in this <code>Suite</code>.
   * @param args the <code>Args</code> for this run
   * @return a <code>Status</code> object that indicates when all tests and nested suites started by this method have completed, and whether or not a failure occurred.
   */
  abstract override def run(testName: Option[String], args: Args): Status = {
    val testServer = TestServer(port, app)
    try {
      testServer.start()
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app) + ("org.scalatestplus.play.port" -> port)
      val newArgs = args.copy(configMap = newConfigMap)
      super.run(testName, newArgs)
    } finally testServer.stop()
  }
}


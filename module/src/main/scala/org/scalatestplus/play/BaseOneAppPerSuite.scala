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
package org.scalatestplus.play

import org.scalatest._
import play.api.Application
import play.api.Play

/**
 * The base abstract trait for one app per suite.
 */
trait BaseOneAppPerSuite extends SuiteMixin with AppProvider with BeforeAndAfterAll with BeforeAndAfterEachTestData {
  this: Suite with FakeApplicationFactory =>

  @volatile private var privateApp: Application = _

  /**
   * An implicit instance of `Application`.
   */
  final implicit def app: Application = {
    require(privateApp != null, "Test isn't running yet so application is not available")
    privateApp
  }

  protected override def beforeAll: Unit = {
    privateApp = fakeApplication()
    Play.start(app)
    super.beforeAll()
  }

  protected override def afterAll(): Unit = {
    try {
      super.afterAll()
    } finally {
      val theApp = app
      privateApp = null
      Play.stop(theApp)
    }
  }

  /**
   * Places a reference to the app into per-test instances
   */
  protected override def beforeEach(testData: TestData): Unit = {
    if (isInstanceOf[OneInstancePerTest])
      setApplicationFrom(testData.configMap)
    super.beforeEach(testData)
  }

  private def setApplicationFrom(configMap: ConfigMap): Unit = {
    synchronized { privateApp = providerFrom(configMap).app }
  }

  /**
   * Places the app into the test's ConfigMap
   */
  abstract override def testDataFor(testName: String, configMap: ConfigMap): TestData = {
    super.testDataFor(testName, configMap + ("org.scalatestplus.play.app" -> providerFrom(configMap).app))
  }

  private def providerFrom(configMap: ConfigMap): AppProvider = {
    configMap
      .getOptional[AppProvider]("org.scalatestplus.play.app.provider")
      .getOrElse(
        throw new IllegalArgumentException(
          "BaseOneAppPerSuite needs an Application value associated with key \"org.scalatestplus.play.app.provider\" in the config map"
        )
      )
  }

  //put a provider into the config map(instead of app directly), so that if tests are excluded, the app is never created
  abstract override def run(testName: Option[String], args: Args): Status = {
    // if a test is running under OneInstancePerTest, the config map will already be set
    if (args.runTestInNewInstance) super.run(testName, args)
    else {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app.provider" -> this)
      val newArgs      = args.copy(configMap = newConfigMap)
      super.run(testName, newArgs)
    }
  }

}

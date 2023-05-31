/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

/**
 * Trait that provides a configured `Application` to the suite into which it is mixed.
 *
 * The purpose of this trait is to allow nested suites of an enclosing suite that extends [[org.scalatestplus.play.guice.GuiceOneAppPerSuite GuiceOneAppPerSuite]]
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
 *       app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
 *     }
 *     "make the FakeApplication available implicitly" in {
 *       def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
 *       getConfig("ehcacheplugin") mustBe Some("disabled")
 *     }
 *   }
 * }
 * </pre>
 */
trait ConfiguredApp extends SuiteMixin with BeforeAndAfterAllConfigMap with BeforeAndAfterEachTestData { this: Suite =>

  @volatile private var configuredApp: Application = _

  /**
   * The "configured" `Application` instance that was passed into `run` via the `ConfigMap`.
   *
   * @return the configured `Application`
   */
  final implicit def app: Application = synchronized { configuredApp }

  /**
   * Looks in `args.configMap` for a key named "org.scalatestplus.play.app" whose value is a `Application`,
   * and if it exists, sets it as the `Application` that will be returned from the `app` method, then calls
   * `super.run`.
   *
   * If no key matches "org.scalatestplus.play.app" in `args.configMap`, or the associated value is
   * not a `Application`, throws `IllegalArgumentException`.
   *
   * @throws java.lang.IllegalArgumentException if the `Application` does not appear in `args.configMap` under the expected key
   */
  protected override def beforeAll(configMap: ConfigMap): Unit = {
    super.beforeAll(configMap)
    setApplicationFrom(configMap)
  }

  /**
   * Places a reference to the app into per-test instances
   */
  protected override def beforeEach(testData: TestData): Unit = {
    super.beforeEach(testData)
    if (isInstanceOf[OneInstancePerTest])
      setApplicationFrom(testData.configMap)
  }

  private def setApplicationFrom(configMap: ConfigMap): Unit = {
    synchronized { configuredApp = providerFrom(configMap).app }
  }

  private def providerFrom(configMap: ConfigMap): AppProvider = {
    configMap
      .getOptional[AppProvider]("org.scalatestplus.play.app.provider")
      .getOrElse(
        throw new IllegalArgumentException(
          "ConfiguredApp needs an Application value associated with key \"org.scalatestplus.play.app.provider\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?"
        )
      )
  }

  /**
   * Places the app into the test's ConfigMap
   */
  abstract override def testDataFor(testName: String, configMap: ConfigMap): TestData = {
    super.testDataFor(testName, configMap + ("org.scalatestplus.play.app" -> providerFrom(configMap).app))
  }

}

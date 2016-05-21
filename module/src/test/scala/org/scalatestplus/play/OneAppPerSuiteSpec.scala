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

import org.scalatest._
import play.api.{Play, Application}
import play.api.inject.guice._

class OneAppPerSuiteSpec extends UnitSpec with OneAppPerSuite {

  def fakeApplication() = new GuiceApplicationBuilder().configure(Map("foo" -> "bar", "ehcacheplugin" -> "disabled")).build()
  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  // Doesn't need synchronization because set by withFixture and checked by the test
  // invoked inside same withFixture with super.withFixture(test)
  var configMap: ConfigMap = _

  override def withFixture(test: NoArgTest): Outcome = {
    configMap = test.configMap
    super.withFixture(test)
  }

  "The OneAppPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in {
      Play.maybeApplication mustBe Some(app)
    }
    "put the app in the configMap" in {
      val configuredApp = configMap.getOptional[Application]("org.scalatestplus.play.app")
      configuredApp.value must be theSameInstanceAs app
    }
  }
}


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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice._
import java.util.concurrent.atomic.AtomicInteger

object OneAppPerSuiteParallelSpec {
  var timesApplicationIsBuilt = new AtomicInteger(0)
}
class OneAppPerSuiteParallelSpec extends UnitSpec with GuiceOneAppPerSuite with ParallelTestExecution {
  import OneAppPerSuiteParallelSpec._
  override def fakeApplication(): Application = {
    timesApplicationIsBuilt.incrementAndGet()
    GuiceApplicationBuilder().configure("foo" -> "bar").build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  // Doesn't need synchronization because set by withFixture and checked by the test
  // invoked inside same withFixture with super.withFixture(test)
  var configMap: ConfigMap = _

  override def withFixture(test: NoArgTest): Outcome = {
    configMap = test.configMap
    super.withFixture(test)
  }

  "The GuiceOneAppPerSuite trait, when running in parallel" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      getConfig("foo") mustBe Some("bar")
    }
    "put the app in the configMap" in {
      val configuredApp = configMap.getOptional[Application]("org.scalatestplus.play.app")
      (configuredApp.value must be).theSameInstanceAs(app)
    }

    //these two together assert that the app is only constructed once
    "must reuse the same app between tests, run1" in {
      timesApplicationIsBuilt.get mustBe 1
    }
    "must reuse the same app between tests, run2" in {
      timesApplicationIsBuilt.get mustBe 1
    }
  }
}

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

package org.scalatestplus.play.examples.guice.oneapppersuite

import org.scalatest.*
import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.*

// This is the "master" suite
class NestedExampleSpec
    extends Suites(new OneSpec, new TwoSpec, new RedSpec, new BlueSpec)
    with GuiceOneAppPerSuite
    with TestSuite {
  // Override fakeApplication if you need an Application with other than non-default parameters.
  override def fakeApplication(): Application = {
    GuiceApplicationBuilder().configure("foo" -> "bar").build()
  }
}

// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp
@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp
@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover
class BlueSpec extends PlaySpec with ConfiguredApp {

  "The GuiceOneAppPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
      getConfig("foo") mustBe Some("bar")
    }
  }
}

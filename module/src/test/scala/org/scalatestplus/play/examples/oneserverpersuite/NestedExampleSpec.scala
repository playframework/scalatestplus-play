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
package org.scalatestplus.play.examples.oneserverpersuite

import play.api.test._
import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.{Application, Play}
import play.api.inject.guice._

 // This is the "master" suite
class NestedExampleSpec extends Suites(
  new OneSpec,
  new TwoSpec,
  new RedSpec,
  new BlueSpec
) with GuiceOneServerPerSuite with TestSuite {
  // Override app if you need an Application with other than non-default parameters.
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
}
 
// These are the nested suites
@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredServer
@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredServer
@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredServer

@DoNotDiscover
class BlueSpec extends PlaySpec with ConfiguredServer {

  "The OneServerPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "make the Application available implicitly" in {
      def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)
      getConfig("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in {
      Play.maybeApplication mustBe Some(app)
    }
    "provide the port number" in {
      port mustBe Helpers.testServerPort
    }
    "provide an actual running server" in {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boum")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
}

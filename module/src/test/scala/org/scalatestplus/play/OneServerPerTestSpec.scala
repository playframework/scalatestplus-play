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
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Application
import play.api.inject.guice._
import play.api.test.RunningServer

class OneServerPerTestSpec extends UnitSpec with GuiceOneServerPerTest with BeforeAndAfterEach {

  private var serverFromBeforeEachTestData: RunningServer = _
  private var appFromBeforeEachTestData: Application      = _

  protected override def beforeEach(testData: TestData): Unit = {
    super.beforeEach(testData)
    appFromBeforeEachTestData = app
    serverFromBeforeEachTestData = runningServer
  }

  override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder().configure("foo" -> "bar").build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  "The OneServerPerTest trait" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in {
      getConfig("foo") mustBe Some("bar")
    }
    "make the Application available in beforeEach(testData)" in {
      appFromBeforeEachTestData mustBe app
    }
    "make the Server available in beforeEach(testData)" in {
      serverFromBeforeEachTestData mustBe runningServer
    }
    "provide an http endpoint" in {
      runningServer.endpoints.httpEndpoint must not be empty
    }
    "send 404 on a bad request" in {
      import java.net._
      val url = new URI("http://localhost:" + port + "/boum").toURL
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
}

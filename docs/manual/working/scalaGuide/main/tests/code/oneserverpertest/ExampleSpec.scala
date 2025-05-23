/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest.oneserverpertest

import org.scalatest.*
import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Application
import play.api.inject.guice.*
import play.api.libs.ws.*
import play.api.mvc.Results.*
import play.api.mvc.*
import play.api.test.Helpers.*

// #scalafunctionaltest-oneserverpertest
class ExampleSpec extends PlaySpec with GuiceOneServerPerTest {

  // Override newAppForTest or mixin GuiceFakeApplicationFactory and use fakeApplication() for an Application
  override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .appRoutes(app => { case ("GET", "/") =>
        app.injector.instanceOf(classOf[DefaultActionBuilder]) {
          Ok("ok")
        }
      })
      .build()
  }

  "The OneServerPerTest trait" must {
    "test server logic" in {
      val wsClient              = app.injector.instanceOf[WSClient]
      val myPublicAddress       = s"localhost:$port"
      val testPaymentGatewayURL = s"http://$myPublicAddress"
      // The test payment gateway requires a callback to this server before it returns a result...
      val callbackURL = s"http://$myPublicAddress/callback"
      // await is from play.api.test.FutureAwaits
      val response =
        await(wsClient.url(testPaymentGatewayURL).addQueryStringParameters("callbackURL" -> callbackURL).get())

      response.status mustBe OK
    }
  }
}
// #scalafunctionaltest-oneserverpertest

/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.oneserverpersuite

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice._
import play.api.libs.ws._
import play.api.mvc.Results._
import play.api.mvc._
import play.api.test.Helpers._

// #scalafunctionaltest-oneserverpersuite
class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite {

  // Override app if you need an Application with other than
  // default parameters.
  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .appRoutes(app => {
        case ("GET", "/") => app.injector.instanceOf(classOf[DefaultActionBuilder]) { Ok("ok") }
      })
      .build()
  }

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
// #scalafunctionaltest-oneserverpersuite

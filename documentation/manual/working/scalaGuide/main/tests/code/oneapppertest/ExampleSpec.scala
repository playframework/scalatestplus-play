/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.oneapppertest

import org.scalatest._
import org.scalatestplus.play._
import play.api.Play
import play.api.inject.guice._

// #scalafunctionaltest-oneapppertest
class ExampleSpec extends PlaySpec with OneAppPerTest {

  // Override newAppForTest if you need an Application with other than
  // default parameters.
  implicit override def newAppForTest(td: TestData) = new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()

  "The OneAppPerTest trait" must {
    "provide a new Application for each test" in {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in {
      Play.maybeApplication mustBe Some(app)
    }
  }
}
// #scalafunctionaltest-oneapppertest

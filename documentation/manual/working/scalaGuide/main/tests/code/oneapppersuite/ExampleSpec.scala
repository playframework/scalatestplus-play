/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.oneapppersuite

import org.scalatestplus.play._
import play.api.Play
import play.api.inject.guice._

// #scalafunctionaltest-oneapppersuite
class ExampleSpec extends PlaySpec with OneAppPerSuite {

  // Override app if you need a Application with other than
  // default parameters.
  implicit override lazy val app = new GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()

  "The OneAppPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getString("ehcacheplugin") mustBe Some("disabled")
    }
    "start the Application" in {
      Play.maybeApplication mustBe Some(app)
    }
  }
}
// #scalafunctionaltest-oneapppersuite

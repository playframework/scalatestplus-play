/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest.oneapppersuite

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice._

// #scalafunctionaltest-oneapppersuite
class ExampleSpec extends PlaySpec with GuiceOneAppPerSuite {

  // Override fakeApplication if you need a Application with other than
  // default parameters.
  override def fakeApplication(): Application = {
    GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
  }

  "The GuiceOneAppPerSuite trait" must {
    "provide an Application" in {
      app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
  }
}
// #scalafunctionaltest-oneapppersuite

/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest.oneapppertest

import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice._

// #scalafunctionaltest-oneapppertest
class ExampleSpec extends PlaySpec with GuiceOneAppPerTest {

  // Override newAppForTest if you need an Application with other than
  // default parameters.
  override def newAppForTest(td: TestData): Application = {
    GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
  }

  "The OneAppPerTest trait" must {
    "provide a new Application for each test" in {
      app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
    }
  }
}
// #scalafunctionaltest-oneapppertest

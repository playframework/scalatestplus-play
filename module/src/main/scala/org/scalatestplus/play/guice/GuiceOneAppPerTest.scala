package org.scalatestplus.play.guice

import org.scalatest.{Suite, TestData}
import org.scalatestplus.play.OneAppPerTest
import play.api.Application

trait GuiceOneAppPerTest extends OneAppPerTest with GuiceFakeApplicationFactory { this: Suite =>

  /**
   * Creates new instance of `Application` with parameters set to their defaults.
   *
   * Override this method if you need a `Application` created with non-default parameter values.
   */
  def newAppForTest(testData: TestData): Application = fakeApplication()

}

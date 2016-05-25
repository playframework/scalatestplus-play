package org.scalatestplus.play.guice

import org.scalatest.Suite
import org.scalatestplus.play.BaseOneServerPerSuite
import play.api.Application

trait GuiceOneServerPerSuite extends BaseOneServerPerSuite with GuiceFakeApplicationFactory { this: Suite =>

  /**
   * An implicit instance of `Application`.
   *
   * This trait's implementation initializes this `lazy` `val` with a new instance of `Application` with
   * parameters set to their defaults, using the `fakeApplication()` method from `FakeApplicationFactory`.
   *
   * Override this `lazy` `val` if you need a `Application` created with non-default parameter values.
   */
  implicit lazy val app: Application = fakeApplication()

}

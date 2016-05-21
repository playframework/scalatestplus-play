package org.scalatestplus.play

import play.api.Application

/**
 * Trait that provides method that creates a new instance of `Application` to the functional test suite mixins.
 *
 * The `GuiceFakeApplicationFactory` provides a `FakeApplicationFactory` that uses `GuiceApplicationBuilder`
 * to build an Application for test suites.
 */
trait FakeApplicationFactory {

  def fakeApplication(): Application

}

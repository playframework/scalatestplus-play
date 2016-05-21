package org.scalatestplus.play.guice

import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * This trait is used to return an instance of Application that is used in tests.
 *
 * If you need to use a fake application for Play, but don't want to override the
 * application method for each suite individually, you can create a trait that
 * subclasses GuiceFakeApplicationFactory
 */
trait GuiceFakeApplicationFactory extends FakeApplicationFactory {

  def fakeApplication(): Application = new GuiceApplicationBuilder().build()

}

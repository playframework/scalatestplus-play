package org.scalatestplus.play.components

import org.scalatest.TestSuite
import org.scalatestplus.play.{ BaseOneAppPerSuite, FakeApplicationFactory }
import play.api.{ Application, BuiltInComponents }

/**
 * An extension of [[BaseOneAppPerSuite]] providing Compile-time DI.
 */
trait OneAppPerSuiteWithComponents
    extends BaseOneAppPerSuite
    with WithApplicationComponents
    with FakeApplicationFactory {
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

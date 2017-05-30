package org.scalatestplus.play.components

import org.scalatest.TestSuite
import org.scalatestplus.play.{ BaseOneServerPerSuite, FakeApplicationFactory }
import play.api.{ Application, BuiltInComponents }

/**
 * An extension of [[BaseOneServerPerSuite]] providing Compile-time DI.
 */
trait OneServerPerSuiteWithComponents
    extends BaseOneServerPerSuite
    with WithApplicationComponents
    with FakeApplicationFactory {
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

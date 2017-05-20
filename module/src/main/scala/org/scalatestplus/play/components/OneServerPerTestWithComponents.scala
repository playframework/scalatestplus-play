package org.scalatestplus.play.components

import org.scalatest.TestSuite
import org.scalatestplus.play.{ BaseOneServerPerTest, FakeApplicationFactory }
import play.api.{ Application, BuiltInComponents }

/**
 * An extension of [[BaseOneServerPerTest]] providing Compile-time DI.
 */
trait OneServerPerTestWithComponents[T <: BuiltInComponents]
    extends BaseOneServerPerTest
    with WithApplicationComponents
    with FakeApplicationFactory {
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

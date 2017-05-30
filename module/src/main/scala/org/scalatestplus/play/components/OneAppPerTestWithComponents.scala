package org.scalatestplus.play.components

import org.scalatest.{ TestSuite, TestSuiteMixin }
import org.scalatestplus.play.{ BaseOneAppPerTest, FakeApplicationFactory }
import play.api.{ Application, BuiltInComponents }

/**
 * An extension of [[BaseOneAppPerTest]] providing Compile-time DI.
 */
trait OneAppPerTestWithComponents
    extends BaseOneAppPerTest
    with WithApplicationComponents
    with FakeApplicationFactory with TestSuiteMixin {
  this: TestSuite =>

  override def fakeApplication(): Application = newApplication
}

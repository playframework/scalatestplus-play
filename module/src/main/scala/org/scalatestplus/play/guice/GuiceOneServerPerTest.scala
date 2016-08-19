package org.scalatestplus.play.guice

import org.scalatest.TestSuite
import org.scalatestplus.play.BaseOneServerPerTest

trait GuiceOneServerPerTest extends BaseOneServerPerTest with GuiceFakeApplicationFactory { this: TestSuite =>

}

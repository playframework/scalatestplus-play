package org.scalatestplus.play.guice

import org.scalatest.TestSuite
import org.scalatestplus.play.BaseOneServerPerSuite

trait GuiceOneServerPerSuite extends BaseOneServerPerSuite with GuiceFakeApplicationFactory { this: TestSuite =>

}

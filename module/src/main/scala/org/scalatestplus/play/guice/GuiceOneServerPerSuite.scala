package org.scalatestplus.play.guice

import org.scalatest.Suite
import org.scalatestplus.play.BaseOneServerPerSuite

trait GuiceOneServerPerSuite extends BaseOneServerPerSuite with GuiceFakeApplicationFactory { this: Suite =>

}

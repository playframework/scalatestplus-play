package org.scalatestplus.play.guice

import org.scalatest.Suite
import org.scalatestplus.play.OneAppPerSuite

trait GuiceOneAppPerSuite extends OneAppPerSuite with GuiceFakeApplicationFactory { this: Suite =>

}

package org.scalatestplus.play.guice

import org.scalatest.Suite
import org.scalatestplus.play.OneServerPerSuite

trait GuiceOneServerPerSuite extends OneServerPerSuite with GuiceFakeApplicationFactory { this: Suite =>

}

package org.scalatestplus.play.guice

import org.scalatest.Suite
import org.scalatestplus.play.BaseOneServerPerSuite
import play.api.Application

trait GuiceOneServerPerSuite extends BaseOneServerPerSuite with GuiceFakeApplicationFactory { this: Suite =>

}

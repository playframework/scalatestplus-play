package org.scalatestplus.play.guice

import org.scalatest.{Suite, TestData}
import org.scalatestplus.play.BaseOneServerPerTest

trait GuiceOneServerPerTest extends BaseOneServerPerTest with GuiceFakeApplicationFactory { this: Suite =>

}

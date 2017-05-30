/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package org.scalatestplus.play.examples.components.oneapppersuite

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import org.scalatestplus.play.examples.components.SomeAppComponents
import play.api.mvc.Result
import play.api.test.Helpers.{ route, _ }
import play.api.test.{ FakeRequest, Helpers }

import scala.concurrent.Future

class ExampleSpec extends PlaySpec with OneAppPerSuiteWithComponents {

  //This override provides the Application components for the tests
  override def components = new SomeAppComponents(context)

  "The ComponentsOneAppPerSuite trait" must {
    "provide an Application" in {
      val Some(result): Option[Future[Result]] = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
  }
}

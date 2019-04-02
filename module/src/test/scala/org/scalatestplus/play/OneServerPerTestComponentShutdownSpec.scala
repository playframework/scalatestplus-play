/*
 * Copyright 2001-2016 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatestplus.play

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.components.OneServerPerTestWithComponents
import play.api.ApplicationLoader.Context
import play.api._
import play.api.mvc.Results
import play.api.routing.Router

import scala.concurrent.Future

class OneServerPerTestComponentShutdownSpec extends UnitSpec with OneServerPerTestWithComponents with BeforeAndAfterAll {

  override def components: BuiltInComponents = new TestComponents(context)

  class TestComponents(context: Context) extends BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    lazy val router: Router = Router.from({
      case _ => defaultActionBuilder {
        Results.Ok("success!")
      }
    })

    applicationLifecycle.addStopHook(() => {
      Future.successful(shutDownCounter.incrementAndGet())
    })
  }

  override protected def afterAll(): Unit = {
    shutDownCounter.get() mustBe 2
    super.afterAll()
  }

  private val shutDownCounter = new AtomicInteger()

  "The OneServerPerTestWithComponents trait" must {
    "shutdown application correctly after each test" in {
      app
    }

    "shutdown application correctly after this test too" in {
      app
    }
  }

}


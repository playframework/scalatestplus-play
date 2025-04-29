/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalatestplus.play

import java.util.concurrent.TimeoutException

import org.scalatestplus.play.components.OneServerPerTestWithComponents
import play.api.mvc.Results
import play.api.routing.Router
import play.api.BuiltInComponents
import play.api.BuiltInComponentsFromContext
import play.api.NoHttpFiltersComponents

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.Await
import scala.concurrent.Future
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class OneServerPerTestComponentSynchronizationSpec
    extends AnyFlatSpec
    with Matchers
    with OneServerPerTestWithComponents {

  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    lazy val router: Router = Router.from { case _ =>
      defaultActionBuilder {
        Results.Ok("success!")
      }
    }

  }

  lazy val sum: Int = 1 + 1

  "A asynchronous test based on OneServerPerTestWithComponents trait" must "not result in dead lock when the test initializes lazy val" in {
    val action = Future {
      sum mustBe 2
    }

    try {
      Await.result(action, 1.second)
    } catch {
      case _: TimeoutException =>
        fail()
    }
    succeed
  }

}

/*
 * Copyright 2001-2014 Artima, Inc.
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

import play.api.test._
import org.scalatest._
import play.api.Play

trait ConfiguredServer extends SuiteMixin { this: Suite => 

  private var configuredApp: FakeApplication = _
  implicit final def app: FakeApplication = synchronized { configuredApp }

  private var configuredPort: Int = -1
  def port: Int = synchronized { configuredPort } 

  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[FakeApplication]("app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new Exception("ConfiguredServer needs a FakeApplication value associated with key \"app\" in the config map")
    }
    args.configMap.getOptional[Int]("port") match {
      case Some(cp) => synchronized { configuredPort = cp }
      case None => throw new Exception("ConfiguredServer needs an Int value associated with key \"port\" in the config map")
    }
    super.run(testName, args)
  }
}


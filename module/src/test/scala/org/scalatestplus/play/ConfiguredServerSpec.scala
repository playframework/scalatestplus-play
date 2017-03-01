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

import play.api.test._
import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice._

class ConfiguredServerSpec extends UnitSpec with SequentialNestedSuiteExecution with GuiceOneServerPerSuite {

  override def nestedSuites = Vector(new ConfiguredServerNestedSuite)

  override def fakeApplication(): Application = new GuiceApplicationBuilder().configure(Map("foo" -> "bar", "ehcacheplugin" -> "disabled")).build()
  def getConfig(key: String)(implicit app: Application) = app.configuration.getOptional[String](key)
}

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
import selenium.WebBrowser
import org.openqa.selenium.WebDriver
import concurrent.Eventually
import concurrent.IntegrationPatience

trait ConfiguredBrowser extends SuiteMixin with WebBrowser with Eventually with IntegrationPatience { this: Suite => 

  private var configuredApp: FakeApplication = _
  implicit final def app: FakeApplication = synchronized { configuredApp }

  private var configuredPort: Int = -1
  def port: Int = synchronized { configuredPort } 

  private var configuredWebDriver: WebDriver = _
  implicit def webDriver: WebDriver = synchronized { configuredWebDriver } 

  abstract override def run(testName: Option[String], args: Args): Status = {
    args.configMap.getOptional[FakeApplication]("org.scalatestplus.play.app") match {
      case Some(ca) => synchronized { configuredApp = ca }
      case None => throw new Exception("ConfiguredBrowser needs a FakeApplication value associated with key \"org.scalatestplus.play.app\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    args.configMap.getOptional[Int]("org.scalatestplus.play.port") match {
      case Some(cp) => synchronized { configuredPort = cp }
      case None => throw new Exception("ConfiguredBrowser needs an Int value associated with key \"org.scalatestplus.play.port\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    args.configMap.getOptional[WebDriver]("org.scalatestplus.play.webDriver") match {
      case Some(cwd) => synchronized { configuredWebDriver = cwd }
      case None => throw new Exception("ConfiguredBrowser needs a WebDriver value associated with key \"org.scalatestplus.play.webDriver\" in the config map. Did you forget to annotate a nested suite with @DoNotDiscover?")
    }
    super.run(testName, args)
  }
}


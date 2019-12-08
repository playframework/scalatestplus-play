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
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import play.api.http.HttpProtocol
import play.api.http.Status
import play.api.http.HeaderNames
import org.scalatest.wordspec
import org.scalatest.matchers.must.Matchers

/**
 * Convenience "super Suite" class for "mixed fixture" Play tests.
 *
 * This class mixes in trait [[org.scalatestplus.play.MixedFixtures MixedFixtures]], and is therefore convenient
 * when different tests in the same test class need different kinds of fixtures. When different tests in the same class
 * need the same fixture, you're probably better of extending [[org.scalatestplus.play.PlaySpec PlaySpec]] instead.
 */
abstract class MixedPlaySpec
    extends wordspec.FixtureAnyWordSpec
    with Matchers
    with OptionValues
    with MixedFixtures
    with Eventually
    with IntegrationPatience
    with WsScalaTestClient

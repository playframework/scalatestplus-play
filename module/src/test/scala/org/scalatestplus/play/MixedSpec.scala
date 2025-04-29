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

import org.scalatest.*
import concurrent.Eventually
import concurrent.IntegrationPatience
import org.scalatest.wordspec
import org.scalatest.matchers.must.Matchers

/**
 * Play-Test super-suite for test classes that need different kinds of fixtures (App, Server,
 * Browser) in different tests.
 */
abstract class MixedSpec
    extends wordspec.FixtureAnyWordSpec
    with Matchers
    with OptionValues
    with Inside
    with MixedFixtures
    with Eventually
    with IntegrationPatience
    with WsScalaTestClient

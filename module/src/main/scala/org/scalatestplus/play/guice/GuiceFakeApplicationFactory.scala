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

package org.scalatestplus.play.guice

import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * This trait is used to return an instance of Application that is used in tests.
 *
 * If you need to use a fake application for Play, but don't want to override the
 * application method for each suite individually, you can create a trait that
 * subclasses GuiceFakeApplicationFactory
 */
trait GuiceFakeApplicationFactory extends FakeApplicationFactory {

  def fakeApplication(): Application = new GuiceApplicationBuilder().build()

}

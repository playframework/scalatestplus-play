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

/**
 * Wraps a port number of a provided `TestServer` so that it can be made available as an implicit without making an `Int` implicit.
 *
 * An implicit `PortNumber` is made available by traits that provide a `play.api.test.TestServer`: [[org.scalatestplus.play.MixedFixtures MixedFixtures]],
 * [[org.scalatestplus.play.OneBrowserPerSuite OneBrowserPerSuite]],
 * [[org.scalatestplus.play.OneBrowserPerTest OneBrowserPerTest]],   [[org.scalatestplus.play.guice.GuiceOneServerPerSuite GuiceOneServerPerSuite]],
 * and [[org.scalatestplus.play.OneServerPerTest OneServerPerTest]].
 *
 * The implicit `PortNumber` is taken by the methods of [[org.scalatestplus.play.WsScalaTestClient WsScalaTestClient]].
 *
 * @param the port number of a provided `play.api.test.TestServer`
 */
case class PortNumber(value: Int)

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

import org.scalatest._
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class AllBrowsersPerSuiteBehaviorSpec extends WordSpec {

  "The AllBrowsersPerSuite trait" must {

    "run all tests with different browsers available on the system one by one" in {
      class TestSpec extends UnitSpec with AllBrowsersPerSuite {
        "test 1" in {}
        "test 2" in {}
      }

      val chrome = try { val d = new ChromeDriver(); d.close(); 1 } catch { case ex: Throwable => 0 }
      val firefox = try { val d = new FirefoxDriver(new FirefoxProfile); d.close(); 1 } catch { case ex: Throwable => 0 }
      val internetExplorer =  try { val d = new InternetExplorerDriver; d.close(); 1 } catch { case ex: Throwable => 0 }
      val safari = try { val d = new SafariDriver; d.close(); 1 } catch { case ex: Throwable => 0 }
      val htmlUnit =
        try {
          val d = new HtmlUnitDriver()
          d.setJavascriptEnabled(true)
          d.close()
          1
        }
        catch {
          case ex: Throwable => 0
        }

      val availableBrowserCount = chrome + firefox + internetExplorer + safari + htmlUnit
      val expectedTestStartingCount = 10 //5 * 2
      val expectedTestSucceededCount = availableBrowserCount * 2
      val expectedTestCanceledCount = (5 - availableBrowserCount) * 2

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")
      assert(testStartingEventsReceived(2).testName == "test 1")
      assert(testStartingEventsReceived(3).testName == "test 2")
      assert(testStartingEventsReceived(4).testName == "test 1")
      assert(testStartingEventsReceived(5).testName == "test 2")
      assert(testStartingEventsReceived(6).testName == "test 1")
      assert(testStartingEventsReceived(7).testName == "test 2")
    }

  }

}
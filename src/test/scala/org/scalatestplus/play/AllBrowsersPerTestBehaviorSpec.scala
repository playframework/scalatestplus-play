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

class AllBrowsersPerTestBehaviorSpec extends WordSpec {

  object ChosenTest extends Tag("ChosenTest")

  class TestSpec extends UnitSpec with AllBrowsersPerTest {
    def registerSharedTests(forBrowser: ForBrowser) {
      "test 1" + forBrowser.name in {}
      "test 2" + forBrowser.name taggedAs(ChosenTest) in {}
    }
    "test 3" taggedAs(ChosenTest) in {}
  }

  "The AllBrowsersPerTest trait" must {

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

    "run all tests with different browsers available on the system one by one" in {

      val expectedTestStartingCount = 11 //5 * 2 + 1
      val expectedTestSucceededCount = availableBrowserCount * 2 + 1
      val expectedTestCanceledCount = (5 - availableBrowserCount) * 2

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [Firefox]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Firefox]")
      assert(testStartingEventsReceived(2).testName == "test 1 [Safari]")
      assert(testStartingEventsReceived(3).testName == "test 2 [Safari]")
      assert(testStartingEventsReceived(4).testName == "test 1 [InternetExplorer]")
      assert(testStartingEventsReceived(5).testName == "test 2 [InternetExplorer]")
      assert(testStartingEventsReceived(6).testName == "test 1 [Chrome]")
      assert(testStartingEventsReceived(7).testName == "test 2 [Chrome]")
      assert(testStartingEventsReceived(8).testName == "test 1 [HtmlUnit]")
      assert(testStartingEventsReceived(9).testName == "test 2 [HtmlUnit]")
      assert(testStartingEventsReceived(10).testName == "test 3")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run only chosen test when ChosenTest tag is passed in" in {
      val expectedTestStartingCount = 6 //5 + 1
      val expectedTestSucceededCount = availableBrowserCount + 1
      val expectedTestCanceledCount = 5 - availableBrowserCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("ChosenTest")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 2 [Firefox]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Safari]")
      assert(testStartingEventsReceived(2).testName == "test 2 [InternetExplorer]")
      assert(testStartingEventsReceived(3).testName == "test 2 [Chrome]")
      assert(testStartingEventsReceived(4).testName == "test 2 [HtmlUnit]")
      assert(testStartingEventsReceived(5).testName == "test 3")
    }

    "run only Firefox tests when Firefox tag is passed in" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = firefox * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.Firefox")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [Firefox]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Firefox]")
    }

    "run only Safari tests when Safari tag is passed in" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = safari * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.Safari")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [Safari]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Safari]")
    }

    "run only InternetExplorer tests when InternetExplorer tag is passed in" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = internetExplorer * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.InternetExplorer")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [InternetExplorer]")
      assert(testStartingEventsReceived(1).testName == "test 2 [InternetExplorer]")
    }

    "run only Chrome tests when Chrome tag is passed in" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = chrome * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.Chrome")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [Chrome]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Chrome]")
    }

    "run only HtmlUnit tests when HtmlUnit tag is passed in" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = htmlUnit * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.HtmlUnit")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [HtmlUnit]")
      assert(testStartingEventsReceived(1).testName == "test 2 [HtmlUnit]")
    }

    "run only Firefox and HtmlUnit tests when Firefox and HtmlUnit tag is passed in" in {
      val expectedTestStartingCount = 4
      val expectedTestSucceededCount = (firefox + htmlUnit) * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.Firefox", "org.scalatest.tags.HtmlUnit")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1 [Firefox]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Firefox]")
      assert(testStartingEventsReceived(2).testName == "test 1 [HtmlUnit]")
      assert(testStartingEventsReceived(3).testName == "test 2 [HtmlUnit]")
    }

    "run only HtmlUnit and ChosenTest tests when HtmlUnit and ChosenTest tag is passed in" in {
      val expectedTestStartingCount = 7 // HtmlUnit * 2 + 4 other browsers on test 2, + test 3
      val expectedTestSucceededCount = (htmlUnit * 2) + firefox + chrome + internetExplorer + safari + 1
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("org.scalatest.tags.HtmlUnit", "ChosenTest")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 2 [Firefox]")
      assert(testStartingEventsReceived(1).testName == "test 2 [Safari]")
      assert(testStartingEventsReceived(2).testName == "test 2 [InternetExplorer]")
      assert(testStartingEventsReceived(3).testName == "test 2 [Chrome]")
      assert(testStartingEventsReceived(4).testName == "test 1 [HtmlUnit]")
      assert(testStartingEventsReceived(5).testName == "test 2 [HtmlUnit]")
      assert(testStartingEventsReceived(6).testName == "test 3")
    }

    "run no test when unrelated tag is passed in" in {
      val expectedTestStartingCount = 0
      val expectedTestSucceededCount = 0
      val expectedTestCanceledCount = 0

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, filter = Filter.apply(Some(Set("NoTest")))))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)
    }

  }

}
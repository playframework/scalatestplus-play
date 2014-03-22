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

  "The AllBrowsersPerTest trait" must {

    class TestSpec extends UnitSpec with AllBrowsersPerTest {
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

    "run all tests with different browsers available on the system one by one" in {

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

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with available browsers on the system one by one when -Dbrowsers=\"\", and get an alert about empty browsers string" in {
      val expectedTestStartingCount = 10 //5 * 2
      val expectedTestSucceededCount = availableBrowserCount * 2
      val expectedTestCanceledCount = (5 - availableBrowserCount) * 2

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "")))
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

      val alertProvidedReceived = rep.alertProvidedEventsReceived
      assert(alertProvidedReceived.length == 1)
      assert(alertProvidedReceived(0).message == Resources("emptyBrowsers"))
    }

    "run all tests with available browsers on the system one by one when -Dbrowsers=\"Ab\", and get an alert about invalid characters in browsers string" in {
      val expectedTestStartingCount = 10 //5 * 2
      val expectedTestSucceededCount = availableBrowserCount * 2
      val expectedTestCanceledCount = (5 - availableBrowserCount) * 2

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "Ab")))
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

      val alertProvidedReceived = rep.alertProvidedEventsReceived
      assert(alertProvidedReceived.length == 1)
      assert(alertProvidedReceived(0).message == "Invalid characters 'A' and 'b' found in browsers configuration, they will be ignored.")
    }

    "run all tests with Chrome browsers on the system one by one when -Dbrowsers=\"C\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = chrome * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "C")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Firefox browsers on the system one by one when -Dbrowsers=\"F\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = firefox * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "F")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Internet Explorer browsers on the system one by one when -Dbrowsers=\"I\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = internetExplorer * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "I")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Safari browsers on the system one by one when -Dbrowsers=\"S\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = safari * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "S")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with HtmlUnit browsers on the system one by one when -Dbrowsers=\"H\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = htmlUnit * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "H")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with HtmlUnit and Firefox browsers on the system one by one when -Dbrowsers=\"HF\"" in {
      val expectedTestStartingCount = 4
      val expectedTestSucceededCount = (htmlUnit + firefox) * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "HF")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")
      assert(testStartingEventsReceived(2).testName == "test 1")
      assert(testStartingEventsReceived(3).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Chrome browsers on the system one by one when -Dbrowsers=\"c\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = chrome * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "c")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Firefox browsers on the system one by one when -Dbrowsers=\"f\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = firefox * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "f")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Internet Explorer browsers on the system one by one when -Dbrowsers=\"i\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = internetExplorer * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "i")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with Safari browsers on the system one by one when -Dbrowsers=\"s\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = safari * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "s")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with HtmlUnit browsers on the system one by one when -Dbrowsers=\"h\"" in {
      val expectedTestStartingCount = 2
      val expectedTestSucceededCount = htmlUnit * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "h")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with HtmlUnit and Firefox browsers on the system one by one when -Dbrowsers=\"hf\"" in {
      val expectedTestStartingCount = 4
      val expectedTestSucceededCount = (htmlUnit + firefox) * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "hf")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")
      assert(testStartingEventsReceived(2).testName == "test 1")
      assert(testStartingEventsReceived(3).testName == "test 2")

      assert(rep.alertProvidedEventsReceived.length == 0)
    }

    "run all tests with HtmlUnit and Firefox browsers on the system one by one and alert that 'A' is invalid for browsers string when -Dbrowsers=\"HAF\"" in {
      val expectedTestStartingCount = 4
      val expectedTestSucceededCount = (htmlUnit + firefox) * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "HAF")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")
      assert(testStartingEventsReceived(2).testName == "test 1")
      assert(testStartingEventsReceived(3).testName == "test 2")

      val alertProvidedEventsReceived = rep.alertProvidedEventsReceived
      assert(alertProvidedEventsReceived.length == 1)
      assert(alertProvidedEventsReceived(0).message == "Invalid character 'A' found in browsers configuration, it will be ignored.")
    }

    "run all tests with HtmlUnit and Firefox browsers on the system one by one and alert that 'A' and 'B' is invalid for browsers string when -Dbrowsers=\"HABF\"" in {
      val expectedTestStartingCount = 4
      val expectedTestSucceededCount = (htmlUnit + firefox) * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "HABF")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")
      assert(testStartingEventsReceived(2).testName == "test 1")
      assert(testStartingEventsReceived(3).testName == "test 2")

      val alertProvidedEventsReceived = rep.alertProvidedEventsReceived
      assert(alertProvidedEventsReceived.length == 1)
      assert(alertProvidedEventsReceived(0).message == "Invalid characters 'A' and 'B' found in browsers configuration, they will be ignored.")
    }

    "run all tests with HtmlUnit and Firefox browsers on the system one by one and alert that 'A', 'B' and 'Z' is invalid for browsers string when -Dbrowsers=\"HABFZ\"" in {
      val expectedTestStartingCount = 4
      val expectedTestSucceededCount = (htmlUnit + firefox) * 2
      val expectedTestCanceledCount = expectedTestStartingCount - expectedTestSucceededCount

      val rep = new EventRecordingReporter
      val spec = new TestSpec
      spec.run(None, Args(reporter = rep, configMap = ConfigMap("browsers" -> "HABFZ")))
      val testStartingEventsReceived = rep.testStartingEventsReceived
      assert(testStartingEventsReceived.length == expectedTestStartingCount)
      assert(rep.testSucceededEventsReceived.length == expectedTestSucceededCount)
      assert(rep.testCanceledEventsReceived.length == expectedTestCanceledCount)

      assert(testStartingEventsReceived(0).testName == "test 1")
      assert(testStartingEventsReceived(1).testName == "test 2")
      assert(testStartingEventsReceived(2).testName == "test 1")
      assert(testStartingEventsReceived(3).testName == "test 2")

      val alertProvidedEventsReceived = rep.alertProvidedEventsReceived
      assert(alertProvidedEventsReceived.length == 1)
      assert(alertProvidedEventsReceived(0).message == "Invalid characters 'A', 'B' and 'Z' found in browsers configuration, they will be ignored.")
    }

  }

}
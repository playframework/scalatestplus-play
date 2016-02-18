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
import play.api.{Play, Application}
import play.api.mvc.Handler

class MixedFixtureSpec extends MixedSpec {

  def fakeApp[A](elems: (String, String)*) = FakeApplication(additionalConfiguration = Map(elems:_*), withRoutes = TestRoute)
  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  "The App function" must {
    "provide a FakeApplication" in new App(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        "test 1" in new App(new TestFakeApplication()) { t =>
          assert(count == 1)
        }
        "test 2" in new App(new TestFakeApplication()) { t =>
          assert(count == 1)
        }
        "test 3" in new App(new TestFakeApplication()) { t =>
          assert(count == 1)
        }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      count mustBe 3
    }
  }
  "The Server function" must {
    "provide a FakeApplication" in new Server(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new Server(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new Server(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        "test 1" in new Server(new TestFakeApplication()) { t =>
          assert(count == 1)
        }
        "test 2" in new Server(new TestFakeApplication()) { t =>
          assert(count == 1)
        }
        "test 3" in new Server(new TestFakeApplication()) { t =>
          assert(count == 1)
        }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      count mustBe 3
    }
    import Helpers._
    "send 404 on a bad request" in new Server {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
  "The HtmlUnit function" must {
    "provide a FakeApplication" in new HtmlUnit(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new HtmlUnit(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new HtmlUnit(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new HtmlUnit(new TestFakeApplication()) { t => testRun = true }
        "test 2" in new HtmlUnit(new TestFakeApplication()) { t => testRun = true }
        "test 3" in new HtmlUnit(new TestFakeApplication()) { t => testRun = true }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      if (spec.testRun)
        count mustBe 3
      else
        count mustBe 0  // when driver not available, not Application instance should be created at all.
    }
    import Helpers._
    "send 404 on a bad request" in new HtmlUnit {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new HtmlUnit(fakeApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Firefox function" must {
    "provide a FakeApplication" in new Firefox(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new Firefox(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new Firefox(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Firefox(new TestFakeApplication()) { t => testRun = true }
        "test 2" in new Firefox(new TestFakeApplication()) { t => testRun = true }
        "test 3" in new Firefox(new TestFakeApplication()) { t => testRun = true }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      if (spec.testRun)
        count mustBe 3
      else
        count mustBe 0  // when driver not available, not Application instance should be created at all.
    }
    import Helpers._
    "send 404 on a bad request" in new Firefox {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Firefox(fakeApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Safari function" must {
    "provide a FakeApplication" in new Safari(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new Safari(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new Safari(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Safari(new TestFakeApplication()) { t => testRun = true }
        "test 2" in new Safari(new TestFakeApplication()) { t => testRun = true }
        "test 3" in new Safari(new TestFakeApplication()) { t => testRun = true }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      if (spec.testRun)
        count mustBe 3
      else
        count mustBe 0  // when driver not available, not Application instance should be created at all.
    }
    import Helpers._
    "send 404 on a bad request" in new Safari {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Safari(fakeApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Chrome function" must {
    "provide a FakeApplication" in new Chrome(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new Chrome(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new Chrome(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Chrome(new TestFakeApplication()) { t => testRun = true }
        "test 2" in new Chrome(new TestFakeApplication()) { t => testRun = true }
        "test 3" in new Chrome(new TestFakeApplication()) { t => testRun = true }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      if (spec.testRun)
        count mustBe 3
      else
        count mustBe 0  // when driver not available, not Application instance should be created at all.
    }
    import Helpers._
    "send 404 on a bad request" in new Chrome {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Chrome(fakeApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The InternetExplorer function" must {
    "provide a FakeApplication" in new InternetExplorer(fakeApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the FakeApplication available implicitly" in new InternetExplorer(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the FakeApplication" in new InternetExplorer(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the FakeApplication lazily" in new App(fakeApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestFakeApplication(override val path: java.io.File = new java.io.File("."),
                                override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
                                additionalPlugins: Seq[String] = Nil,
                                withoutPlugins: Seq[String] = Nil,
                                additionalConfiguration: Map[String, _ <: Any] = Map.empty,
                                withGlobal: Option[play.api.GlobalSettings] = None,
                                withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty)
        extends FakeApplication(path, classloader, additionalPlugins, withoutPlugins, additionalConfiguration, withGlobal, withRoutes) {
        count = count + 1
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new InternetExplorer(new TestFakeApplication()) { t => testRun = true }
        "test 2" in new InternetExplorer(new TestFakeApplication()) { t => testRun = true }
        "test 3" in new InternetExplorer(new TestFakeApplication()) { t => testRun = true }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      if (spec.testRun)
        count mustBe 3
      else
        count mustBe 0  // when driver not available, not Application instance should be created at all.
    }
    import Helpers._
    "send 404 on a bad request" in new InternetExplorer {
      import java.net._
      val url = new URL("http://localhost:" + port + "/boom")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new InternetExplorer(fakeApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "Any old thing" must {
    "be doable without much boilerplate" in { () =>
       1 + 1 mustEqual 2
     }
  }
}


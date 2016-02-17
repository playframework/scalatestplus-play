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

import akka.actor.ActorSystem
import play.api.http.{HttpErrorHandler, HttpRequestHandler}
import play.api.inject.Injector
import play.api.test._
import play.api._
import play.api.inject.guice._
import play.api.routing._
import org.scalatest._

import scala.concurrent.Future

class MixedFixtureSpec extends MixedSpec {

  def buildApp[A](elems: (String, String)*) =
    new GuiceApplicationBuilder().configure(Map(elems:_*)).additionalRouter(Router.from(TestRoute)).build()
  def getConfig(key: String)(implicit app: Application) = app.configuration.getString(key)

  "The App function" must {
    "provide a Application" in new App(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        "test 1" in new App(new TestApplication()) { t => }
        "test 2" in new App(new TestApplication()) { t => }
        "test 3" in new App(new TestApplication()) { t => }
      }
      val spec = new TestSpec
      count mustBe 0
      spec.run(None, Args(SilentReporter))
      count mustBe 3
    }
  }
  "The Server function" must {
    "provide a Application" in new Server(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Server(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Server(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        "test 1" in new Server(new TestApplication()) { t => }
        "test 2" in new Server(new TestApplication()) { t => }
        "test 3" in new Server(new TestApplication()) { t => }
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
    "provide an Application" in new HtmlUnit(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new HtmlUnit(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new HtmlUnit(new TestApplication()) { t => testRun = true }
        "test 2" in new HtmlUnit(new TestApplication()) { t => testRun = true }
        "test 3" in new HtmlUnit(new TestApplication()) { t => testRun = true }
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
    "provide a web driver" in new HtmlUnit(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Firefox function" must {
    "provide an Application" in new Firefox(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Firefox(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Firefox(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Firefox(new TestApplication()) { t => testRun = true }
        "test 2" in new Firefox(new TestApplication()) { t => testRun = true }
        "test 3" in new Firefox(new TestApplication()) { t => testRun = true }
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
    "provide a web driver" in new Firefox(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Safari(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Safari(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Safari(new TestApplication()) { t => testRun = true }
        "test 2" in new Safari(new TestApplication()) { t => testRun = true }
        "test 3" in new Safari(new TestApplication()) { t => testRun = true }
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
    "provide a web driver" in new Safari(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Chrome(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new Chrome(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Chrome(new TestApplication()) { t => testRun = true }
        "test 2" in new Chrome(new TestApplication()) { t => testRun = true }
        "test 3" in new Chrome(new TestApplication()) { t => testRun = true }
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
    "provide a web driver" in new Chrome(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("foo" -> "bar", "ehcacheplugin" -> "disabled")) {
      app.configuration.getString("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application" in new InternetExplorer(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      Play.maybeApplication mustBe Some(app)
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar",  "ehcacheplugin" -> "disabled")) {
      var count = 0
      class TestApplication extends Application {
        count = count + 1
        private val app: Application = (new GuiceApplicationBuilder()).build()
        override def mode: Mode.Mode = app.mode
        override def configuration: Configuration = app.configuration
        override def actorSystem: ActorSystem = app.actorSystem
        override def requestHandler: HttpRequestHandler = app.requestHandler
        override def errorHandler: HttpErrorHandler = app.errorHandler
        override def stop() = app.stop()
        override def injector: Injector = app.injector
        override def classloader = app.classloader
        implicit override def materializer = app.materializer
        override def path: java.io.File = app.path
      }
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false  // will be false if test is canceled due to driver not available on platform.
        "test 1" in new InternetExplorer(new TestApplication()) { t => testRun = true }
        "test 2" in new InternetExplorer(new TestApplication()) { t => testRun = true }
        "test 3" in new InternetExplorer(new TestApplication()) { t => testRun = true }
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
    "provide a web driver" in new InternetExplorer(buildApp()) {
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


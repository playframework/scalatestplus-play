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

import java.util.concurrent.atomic.AtomicInteger

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.CoordinatedShutdown
import org.apache.pekko.stream.Materializer
import play.api.http.HttpErrorHandler
import play.api.http.HttpRequestHandler
import play.api.inject.Injector
import play.api.*
import play.api.inject.guice.*
import org.scalatest.*
import play.api.mvc.request.RequestFactory

import scala.concurrent.Future
import org.scalatest.wordspec

class MixedFixtureSpec extends MixedSpec {

  class TestApplication(counter: AtomicInteger) extends Application {

    counter.incrementAndGet()

    private val app: Application                          = GuiceApplicationBuilder().build()
    override def mode: Mode                               = app.mode
    override def configuration: Configuration             = app.configuration
    override def actorSystem: ActorSystem                 = app.actorSystem
    override def coordinatedShutdown: CoordinatedShutdown = CoordinatedShutdown(app.actorSystem)
    override def requestHandler: HttpRequestHandler       = app.requestHandler
    override def errorHandler: HttpErrorHandler           = app.errorHandler
    override def stop(): Future[?]                        = app.stop()
    override def injector: Injector                       = app.injector
    override def classloader: ClassLoader                 = app.classloader
    implicit override def materializer: Materializer      = app.materializer
    override def path: java.io.File                       = app.path
    override def environment: Environment                 = app.environment
    override def requestFactory: RequestFactory           = app.requestFactory
  }

  def buildApp[A](elems: (String, String)*): Application = {
    GuiceApplicationBuilder()
      .configure(Map(elems*))
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  "The App function" must {
    "provide a Application" in new App(buildApp("foo" -> "bar")) {
      override def running() =
        app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new App(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()

        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          "test 1" in new App(new TestApplication(counter)) {
            t =>
            override def running() = ()
          }
          "test 2" in new App(new TestApplication(counter)) {
            t =>
            override def running() = ()
          }
          "test 3" in new App(new TestApplication(counter)) {
            t =>
            override def running() = ()
          }
        }
        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        counter.get() mustBe 3
      }
    }
  }
  "The Server function" must {
    "provide a Application" in new Server(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Server(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()

        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          "test 1" in new Server(new TestApplication(counter)) {
            t =>
            override def running() = ()
          }
          "test 2" in new Server(new TestApplication(counter)) {
            t =>
            override def running() = ()
          }
          "test 3" in new Server(new TestApplication(counter)) {
            t =>
            override def running() = ()
          }
        }
        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        counter.get() mustBe 3
      }
    }
    "send 404 on a bad request" in new Server {
      override def running() = {
        import java.net.*
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
  }
  "The HtmlUnit function" must {
    "provide an Application" in new HtmlUnit(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()
        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          var testRun = false // will be false if test is canceled due to driver not available on platform.
          "test 1" in new HtmlUnit(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 2" in new HtmlUnit(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 3" in new HtmlUnit(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
        }

        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        if (spec.testRun)
          counter.get() mustBe 3
        else
          counter.get() mustBe 0 // when driver not available, not Application instance should be created at all.
      }
    }
    "send 404 on a bad request" in new HtmlUnit {
      override def running() = {
        import java.net.*
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new HtmlUnit(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The Firefox function" must {
    "provide an Application" in new Firefox(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Firefox(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()
        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          var testRun = false // will be false if test is canceled due to driver not available on platform.
          "test 1" in new Firefox(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 2" in new Firefox(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 3" in new Firefox(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
        }
        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        if (spec.testRun)
          counter.get() mustBe 3
        else
          counter.get() mustBe 0 // when driver not available, not Application instance should be created at all.
      }
    }
    "send 404 on a bad request" in new Firefox {
      override def running() = {
        import java.net.*
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new Firefox(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Safari(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()
        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          var testRun = false // will be false if test is canceled due to driver not available on platform.
          "test 1" in new Safari(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 2" in new Safari(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 3" in new Safari(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
        }
        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        if (spec.testRun)
          counter.get() mustBe 3
        else
          counter.get() mustBe 0 // when driver not available, not Application instance should be created at all.
      }
    }
    "send 404 on a bad request" in new Safari {
      override def running() = {
        import java.net.*
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new Safari(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Chrome(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()
        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          var testRun = false // will be false if test is canceled due to driver not available on platform.
          "test 1" in new Chrome(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 2" in new Chrome(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 3" in new Chrome(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
        }
        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        if (spec.testRun)
          counter.get() mustBe 3
        else
          counter.get() mustBe 0 // when driver not available, not Application instance should be created at all.
      }
    }
    "send 404 on a bad request" in new Chrome {
      override def running() = {
        import java.net.*
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new Chrome(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("foo" -> "bar")) {
      override def running() = app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("foo" -> "bar")) {
      override def running() = getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      override def running() = {
        val counter = new AtomicInteger()
        class TestSpec extends wordspec.FixtureAnyWordSpec with MixedFixtures {
          var testRun = false // will be false if test is canceled due to driver not available on platform.
          "test 1" in new InternetExplorer(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 2" in new InternetExplorer(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
          "test 3" in new InternetExplorer(new TestApplication(counter)) {
            t =>
            override def running() = testRun = true
          }
        }
        val spec = new TestSpec
        counter.get() mustBe 0
        spec.run(None, Args(SilentReporter))
        if (spec.testRun)
          counter.get() mustBe 3
        else
          counter.get() mustBe 0 // when driver not available, not Application instance should be created at all.
      }
    }
    "send 404 on a bad request" in new InternetExplorer {
      override def running() = {
        import java.net.*
        val url                    = new URI("http://localhost:" + port + "/boom").toURL
        val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
        try con.getResponseCode mustBe 404
        finally con.disconnect()
      }
    }
    "provide a web driver" in new InternetExplorer(buildApp()) {
      override def running() = {
        go to ("http://localhost:" + port + "/testing")
        pageTitle mustBe "Test Page"
        click.on(find(name("b")).value)
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }
  "Any old thing" must {
    "be doable without much boilerplate" in { () =>
      1 + 1 mustEqual 2
    }
  }
}

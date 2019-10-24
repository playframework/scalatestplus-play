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

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.actor.CoordinatedShutdown
import akka.stream.Materializer
import play.api.http.HttpErrorHandler
import play.api.http.HttpRequestHandler
import play.api.inject.Injector
import play.api._
import play.api.inject.guice._
import org.scalatest._
import play.api.mvc.request.RequestFactory

import scala.concurrent.Future

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
    override def stop(): Future[_]                        = app.stop()
    override def injector: Injector                       = app.injector
    override def classloader: ClassLoader                 = app.classloader
    implicit override def materializer: Materializer      = app.materializer
    override def path: java.io.File                       = app.path
    override def environment: Environment                 = app.environment
    override def requestFactory: RequestFactory           = app.requestFactory
  }

  def buildApp[A](elems: (String, String)*): Application = {
    GuiceApplicationBuilder()
      .configure(Map(elems: _*))
      .appRoutes(app => TestRoutes.router(app))
      .build()
  }

  def getConfig(key: String)(implicit app: Application): Option[String] = app.configuration.getOptional[String](key)

  "The App function" must {
    "provide a Application" in new App(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new App(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()

      class TestSpec extends fixture.WordSpec with MixedFixtures {
        "test 1" in new App(new TestApplication(counter)) { t =>
        }
        "test 2" in new App(new TestApplication(counter)) { t =>
        }
        "test 3" in new App(new TestApplication(counter)) { t =>
        }
      }
      val spec = new TestSpec
      counter.get() mustBe 0
      spec.run(None, Args(SilentReporter))
      counter.get() mustBe 3
    }
  }
  "The Server function" must {
    "provide a Application" in new Server(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Server(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()

      class TestSpec extends fixture.WordSpec with MixedFixtures {
        "test 1" in new Server(new TestApplication(counter)) { t =>
        }
        "test 2" in new Server(new TestApplication(counter)) { t =>
        }
        "test 3" in new Server(new TestApplication(counter)) { t =>
        }
      }
      val spec = new TestSpec
      counter.get() mustBe 0
      spec.run(None, Args(SilentReporter))
      counter.get() mustBe 3
    }
    "send 404 on a bad request" in new Server {
      import java.net._
      val url                    = new URL("http://localhost:" + port + "/boom")
      val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
  }
  "The HtmlUnit function" must {
    "provide an Application" in new HtmlUnit(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new HtmlUnit(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false // will be false if test is canceled due to driver not available on platform.
        "test 1" in new HtmlUnit(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 2" in new HtmlUnit(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 3" in new HtmlUnit(new TestApplication(counter)) { t =>
          testRun = true
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
    "send 404 on a bad request" in new HtmlUnit {
      import java.net._
      val url                    = new URL("http://localhost:" + port + "/boom")
      val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new HtmlUnit(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Firefox function" must {
    "provide an Application" in new Firefox(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Firefox(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Firefox(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 2" in new Firefox(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 3" in new Firefox(new TestApplication(counter)) { t =>
          testRun = true
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
    "send 404 on a bad request" in new Firefox {
      import java.net._
      val url                    = new URL("http://localhost:" + port + "/boom")
      val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Firefox(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Safari function" must {
    "provide an Application" in new Safari(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Safari(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Safari(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 2" in new Safari(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 3" in new Safari(new TestApplication(counter)) { t =>
          testRun = true
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
    "send 404 on a bad request" in new Safari {
      import java.net._
      val url                    = new URL("http://localhost:" + port + "/boom")
      val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Safari(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The Chrome function" must {
    "provide an Application" in new Chrome(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new Chrome(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false // will be false if test is canceled due to driver not available on platform.
        "test 1" in new Chrome(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 2" in new Chrome(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 3" in new Chrome(new TestApplication(counter)) { t =>
          testRun = true
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
    "send 404 on a bad request" in new Chrome {
      import java.net._
      val url                    = new URL("http://localhost:" + port + "/boom")
      val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new Chrome(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "The InternetExplorer function" must {
    "provide an Application" in new InternetExplorer(buildApp("foo" -> "bar")) {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
    "make the Application available implicitly" in new InternetExplorer(buildApp("foo" -> "bar")) {
      getConfig("foo") mustBe Some("bar")
    }
    "start the Application lazily" in new App(buildApp("foo" -> "bar")) {
      val counter = new AtomicInteger()
      class TestSpec extends fixture.WordSpec with MixedFixtures {
        var testRun = false // will be false if test is canceled due to driver not available on platform.
        "test 1" in new InternetExplorer(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 2" in new InternetExplorer(new TestApplication(counter)) { t =>
          testRun = true
        }
        "test 3" in new InternetExplorer(new TestApplication(counter)) { t =>
          testRun = true
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
    "send 404 on a bad request" in new InternetExplorer {
      import java.net._
      val url                    = new URL("http://localhost:" + port + "/boom")
      val con: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      try con.getResponseCode mustBe 404
      finally con.disconnect()
    }
    "provide a web driver" in new InternetExplorer(buildApp()) {
      go to ("http://localhost:" + port + "/testing")
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
  "Any old thing" must {
    "be doable without much boilerplate" in { () =>
      1 + 1 mustEqual 2
    }
  }
}

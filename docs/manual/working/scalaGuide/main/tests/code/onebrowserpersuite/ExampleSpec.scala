/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest.onebrowserpersuite

import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.DefaultActionBuilder

// #scalafunctionaltest-onebrowserpersuite
class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  // Override app if you need an Application with other than
  // default parameters.
  override def fakeApplication(): Application = {
    import play.api.http.MimeTypes.*
    import play.api.mvc.Results.*

    GuiceApplicationBuilder()
      .appRoutes(app => { case ("GET", "/testing") =>
        app.injector.instanceOf(classOf[DefaultActionBuilder]) {
          Ok("""
               |<html>
               | <head>
               |   <title>Test Page</title>
               |   <body>
               |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
               |   </body>
               | </head>
               |</html>
            """.stripMargin).as(HTML)
        }
      })
      .build()
  }

  "The OneBrowserPerTest trait" must {
    "provide a web driver" in {
      go to s"http://localhost:$port/testing"
      pageTitle mustBe "Test Page"
      click.on(find(name("b")).value)
      eventually { pageTitle mustBe "scalatest" }
    }
  }
}
// #scalafunctionaltest-onebrowserpersuite

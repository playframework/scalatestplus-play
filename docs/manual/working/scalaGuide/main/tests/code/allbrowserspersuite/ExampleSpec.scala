/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.allbrowserspersuite

import org.scalatestplus.play._
import play.api.mvc._
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._
import play.api.cache.EhCacheModule

// #scalafunctionaltest-allbrowserspersuite
class ExampleSpec extends PlaySpec with OneServerPerSuite with AllBrowsersPerSuite {

  // Override app if you need an Application with other than
  // default parameters.
  implicit override lazy val app =
    new GuiceApplicationBuilder().disable[EhCacheModule].router(Router.from {
      case GET(p"/testing") =>
        Action(
          Results.Ok(
            "<html>" +
              "<head><title>Test Page</title></head>" +
              "<body>" +
              "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
              "</body>" +
              "</html>"
          ).as("text/html")
        )
    }).build

  def sharedTests(browser: BrowserInfo) = {
    "The AllBrowsersPerSuite trait" must {
      "provide a web driver " + browser.name in {
        go to s"http://localhost:$port/testing"
        pageTitle mustBe "Test Page"
        click on find(name("b")).value
        eventually { pageTitle mustBe "scalatest" }
      }
    }
  }
}
// #scalafunctionaltest-allbrowserspersuite

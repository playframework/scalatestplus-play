/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest.onebrowserpersuite

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.mvc._
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._
import play.api.cache.ehcache.EhCacheModule

// #scalafunctionaltest-onebrowserpersuite
class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  // Override app if you need a Application with other than
  // default parameters.
  override def fakeApplication() =
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
    }).build()

  "The OneBrowserPerTest trait" must {
    "provide a web driver" in {
      go to s"http://localhost:$port/testing"
      pageTitle mustBe "Test Page"
      click on find(name("b")).value
      eventually { pageTitle mustBe "scalatest" }
    }
  }
}
// #scalafunctionaltest-onebrowserpersuite

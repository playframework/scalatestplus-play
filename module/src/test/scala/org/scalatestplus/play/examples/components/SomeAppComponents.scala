package org.scalatestplus.play.examples.components

import play.api.ApplicationLoader.Context
import play.api.{ BuiltInComponentsFromContext, NoHttpFiltersComponents }
import play.api.mvc.{ Action, Results }
import play.api.routing.Router

/**
 * Simple components class which instantiates an application with a simple router
 * Responding 'Ok' to root level GET requests.
 */
protected class SomeAppComponents(context: Context) extends BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

  import play.api.routing.sird._

  lazy val router: Router = Router.from({
    case GET(p"/") => defaultActionBuilder {
      Results.Ok("success!")
    }
  })
}

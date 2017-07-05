package oneapppertest

import play.api.ApplicationLoader.Context
import play.api.mvc.Results
import play.api.routing.Router
import play.api.{ BuiltInComponentsFromContext, Configuration, NoHttpFiltersComponents }

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

  override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("ehcacheplugin" -> "disabled")
}

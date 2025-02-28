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

package org.scalatestplus.play.examples.components

import play.api.ApplicationLoader.Context
import play.api.mvc.Results
import play.api.routing.Router
import play.api.BuiltInComponentsFromContext
import play.api.NoHttpFiltersComponents

/**
 * Simple components class which instantiates an application with a simple router
 * Responding 'Ok' to root level GET requests.
 */
protected class SomeAppComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with NoHttpFiltersComponents {

  import play.api.routing.sird._

  lazy val router: Router = Router.from({ case GET(p"/") =>
    defaultActionBuilder {
      Results.Ok("success!")
    }
  })
}

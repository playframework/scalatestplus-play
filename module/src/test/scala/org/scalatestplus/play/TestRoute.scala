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

import play.api.*
import play.api.http.MimeTypes
import play.api.mvc.*
import play.api.routing.Router
import play.api.routing.sird.*

object TestRoutes {

  private val Success = Results
    .Ok(
      "<html>" +
        "<head><title>Test Page</title></head>" +
        "<body>" +
        "<input type='button' name='b' value='Click Me' onclick='document.title=\"scalatest\"' />" +
        "</body>" +
        "</html>"
    )
    .as(MimeTypes.HTML)

  def router(implicit app: Application): PartialFunction[(String, String), Handler] = { case ("GET", "/testing") =>
    app.injector.instanceOf(classOf[DefaultActionBuilder]) {
      Success
    }
  }

  def router(Action: DefaultActionBuilder): Router = {
    Router.from { case GET(p"/testing") =>
      Action {
        Success
      }
    }
  }
}

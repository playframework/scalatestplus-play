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
package play.api.test

import play.api.mvc.{Handler, Action, Results}

object TestRoute extends PartialFunction[(String, String), Handler] {

  def apply(v1: (String, String)): Handler = v1 match {
    case ("GET", "/testing") => 
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
  }

  def isDefinedAt(x: (String, String)): Boolean = x._1 == "GET" && x._2 == "/testing"

}

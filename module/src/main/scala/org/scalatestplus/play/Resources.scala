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

import java.text.MessageFormat
import java.util.ResourceBundle

/**
 * Resources for internationalization.
 *
 * @author Bill Venners
 */
private[play] object Resources {

  lazy val resourceBundle = ResourceBundle.getBundle("org.scalatestplus.play.ScalaTestPlusPlayBundle")

  def apply(resourceName: String): String = resourceBundle.getString(resourceName)

  private def makeString(resourceName: String, argArray: Array[Any]): String = {
    val raw    = apply(resourceName)
    val msgFmt = new MessageFormat(raw)
    msgFmt.format(argArray)
  }

  def apply(resourceName: String, o1: Any*): String = {
    makeString(resourceName, o1.toArray)
  }

  def bigProblems(ex: Throwable) = {
    val message = if (ex.getMessage == null) "" else ex.getMessage.trim
    if (message.length > 0) Resources("bigProblemsWithMessage", message) else Resources("bigProblems")
  }
}

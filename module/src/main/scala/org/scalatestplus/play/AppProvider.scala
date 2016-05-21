package org.scalatestplus.play

import play.api.Application

/**
 * Trait that defines an application as `app`.
 */
trait AppProvider {

  implicit def app: Application

}

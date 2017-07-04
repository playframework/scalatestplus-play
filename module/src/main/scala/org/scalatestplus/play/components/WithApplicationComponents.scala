package org.scalatestplus.play.components

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{ BuiltInComponents, _ }

/**
 * A trait that provides a components in scope and returns them when newApplication is called.
 *
 * Mixin one of the public traits in this package to provide the desired functionality.
 *
 * This class has several methods that can be used to customize the behavior in specific ways.
 *
 * This is targeted at functional tests requiring a running application that is bootstrapped using Macwire/Compile time DI.
 * This is provided as an alternative to the [[GuiceApplicationBuilder]] which requires guice bootstrapping.
 *
 * @see https://www.playframework.com/documentation/2.5.x/ScalaFunctionalTestingWithScalaTest#Creating-Application-instances-for-testing
 */
trait WithApplicationComponents {

  /**
   * Override this function to instantiate the components - a factory of sorts.
   *
   * @return the components to be used by the application
   */
  def components: BuiltInComponents

  /**
   * @return new application instance and set the components. This must be called for components to be properly set up.
   */
  final def newApplication: Application = components.application

  /**
   * @return a context to use to create the application.
   */
  lazy val context: ApplicationLoader.Context = {
    val env = Environment.simple()
    ApplicationLoader.createContext(env)
  }
}

package org.scalatestplus.play

import org.scalactic.Equivalence
import play.twirl.api.Html

object Implicits {

  implicit val htmlEquivalence = new Equivalence[Html] {
    def areEquivalent(a: Html, b: Html): Boolean = a.toString == b.toString
  }

}

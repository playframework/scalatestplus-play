package org.scalatestplus.play

import Implicits._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._
import play.twirl.api.Html

class ImplicitsSpec extends FunSpec with Matchers with TypeCheckedTripleEquals {

  describe("htmlEquality with triple equals") {

    it("should compile when types are equal"){
      """Html("hi") === Html("hi")""".stripMargin should compile
    }


    it("should fail to compile when types are unequal"){
      """Html("hi") === 3""".stripMargin shouldNot compile
    }

    it("should return false if Htmls contain different strings when built") {
      Html("hello world") should !==(Html("what"))
    }

    it("should return true if Htmls contain different strings when built") {
      Html("hello world") should ===(Html("hello world"))
    }

  }

}

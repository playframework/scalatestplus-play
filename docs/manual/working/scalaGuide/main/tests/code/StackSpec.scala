/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package scalaguide.tests.scalatest

// #scalatest-stackspec
import org.scalatestplus.play._

import scala.collection.mutable

class StackSpec extends PlaySpec {

  "A Stack" must {
    "pop values in last-in-first-out order" in {
      val stack = new mutable.Stack[Int]
      stack.push(1)
      stack.push(2)
      stack.pop() mustBe 2
      stack.pop() mustBe 1
    }
    "throw NoSuchElementException if an empty stack is popped" in {
      val emptyStack = new mutable.Stack[Int]
      a[NoSuchElementException] must be thrownBy {
        emptyStack.pop()
      }
    }
  }
}
// #scalatest-stackspec

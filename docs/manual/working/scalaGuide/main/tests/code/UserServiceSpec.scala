/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._

import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import scalaguide.tests.models._
import scalaguide.tests.services._

// #scalatest-userservicespec
class UserServiceSpec extends PlaySpec with MockitoSugar {

  "UserService#isAdmin" should {
    "be true when the role is admin" in {
      val userRepository = mock[UserRepository]
      when(userRepository.roles(any[User])).thenReturn(Set(Role("ADMIN")))

      val userService = new UserService(userRepository)

      val actual = userService.isAdmin(User("11", "Steve", "user@example.org"))
      actual mustBe true
    }
  }
}
// #scalatest-userservicespec

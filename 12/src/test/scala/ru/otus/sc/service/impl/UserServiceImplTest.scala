package ru.otus.sc.service.impl

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.UserDao
import ru.otus.sc.model.role.Role
import ru.otus.sc.model.user._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val roleManager = new Role(None, "Manager")
  val roleAdmin   = new Role(None, "Admin")

  val user1: User = User(
    Some(UUID.randomUUID()),
    "userName1",
    "password1",
    "FirstName1",
    "LastName1",
    1,
    Set(roleManager)
  )
  val user2: User = User(
    Some(UUID.randomUUID()),
    "userName2",
    "password2",
    "FirstName2",
    "LastName2",
    2,
    Set(roleAdmin)
  )

  "UserServiceTest tests" - {
    "createUser" - {
      "should create user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)

        (dao.createUser _).expects(user1).returns(Future.successful(user2))

        srv.createUser(CreateUserRequest(user1)).futureValue shouldBe CreateUserResponse(user2)
      }
    }

    "getUser" - {
      "should return user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getUser _).expects(id).returns(Future.successful(Some(user1)))

        srv.getUser(GetUserRequest(id)).futureValue shouldBe GetUserResponse.Found(user1)
      }

      "should return NotFound on unknown user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getUser _).expects(id).returns(Future.successful(None))

        srv.getUser(GetUserRequest(id)).futureValue shouldBe GetUserResponse.NotFound(id)
      }
    }

    "updateUser" - {
      "should update existing user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)

        (dao.updateUser _).expects(user1).returns(Future.successful(Some(user2)))

        srv.updateUser(UpdateUserRequest(user1)).futureValue shouldBe UpdateUserResponse.Updated(
          user2
        )
      }

      "should return NotFound on unknown user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)

        (dao.updateUser _).expects(user1).returns(Future.successful(None))

        srv.updateUser(UpdateUserRequest(user1)).futureValue shouldBe UpdateUserResponse.NotFound(
          user1.id.get
        )
      }

      "should return CantUpdateUserWithoutId on user without id" in {
        val dao  = mock[UserDao]
        val srv  = new UserServiceImpl(dao)
        val user = user1.copy(id = None)

        srv
          .updateUser(UpdateUserRequest(user))
          .futureValue shouldBe UpdateUserResponse.CantUpdateUserWithoutId
      }
    }

    "deleteUser" - {
      "should delete user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteUser _).expects(id).returns(Future.successful(Some(user1)))

        srv.deleteUser(DeleteUserRequest(id)).futureValue shouldBe DeleteUserResponse.Deleted(user1)
      }

      "should return NotFound on unknown user" in {
        val dao = mock[UserDao]
        val srv = new UserServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteUser _).expects(id).returns(Future.successful(None))

        srv.deleteUser(DeleteUserRequest(id)).futureValue shouldBe DeleteUserResponse.NotFound(id)
      }
    }

    "findUsers" - {
      "by last name" - {
        "should return empty list" in {
          val dao      = mock[UserDao]
          val srv      = new UserServiceImpl(dao)
          val lastName = "abc"

          (dao.findByLastName _).expects(lastName).returns(Future.successful(Seq.empty))

          srv
            .findUser(FindUserRequest.ByLastName(lastName))
            .futureValue shouldBe FindUserResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao      = mock[UserDao]
          val srv      = new UserServiceImpl(dao)
          val lastName = "abc"

          (dao.findByLastName _).expects(lastName).returns(Future.successful(Seq(user1, user2)))

          srv
            .findUser(FindUserRequest.ByLastName(lastName))
            .futureValue shouldBe FindUserResponse.Result(
            Seq(user1, user2)
          )
        }
      }
    }
  }
}

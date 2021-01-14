package ru.otus.sc.service.impl

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.Dao
import ru.otus.sc.model._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val roleManager: UUID = UUID.randomUUID()
  val roleAdmin: UUID   = UUID.randomUUID()

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
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)

        (dao.create _).expects(user1).returns(Future.successful(user2))

        srv.create(CreateRequest(user1)).futureValue shouldBe CreateResponse(user2)
      }
    }

    "getUser" - {
      "should return user" in {
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(Some(user1)))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.Found(user1)
      }

      "should return NotFound on unknown user" in {
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(None))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.NotFound(id)
      }
    }

    "updateUser" - {
      "should update existing user" in {
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(user1).returns(Future.successful(Some(user2)))

        srv.update(UpdateRequest(user1)).futureValue shouldBe UpdateResponse.Updated(
          user2
        )
      }

      "should return NotFound on unknown user" in {
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(user1).returns(Future.successful(None))

        srv.update(UpdateRequest(user1)).futureValue shouldBe UpdateResponse.NotFound(
          user1.id.get
        )
      }

      "should return CantUpdateUserWithoutId on user without id" in {
        val dao  = mock[Dao[User]]
        val srv  = new ServiceImpl(dao)
        val user = user1.copy(id = None)

        srv
          .update(UpdateRequest(user))
          .futureValue shouldBe UpdateResponse.CantUpdateWithoutId
      }
    }

    "deleteUser" - {
      "should delete user" in {
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(Some(user1)))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.Deleted(user1)
      }

      "should return NotFound on unknown user" in {
        val dao = mock[Dao[User]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(None))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.NotFound(id)
      }
    }
  }
}

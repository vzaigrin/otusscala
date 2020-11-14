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

class RoleServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {

  val role1: Role = Role(Some(UUID.randomUUID()), "Name1")
  val role2: Role = Role(Some(UUID.randomUUID()), "Name2")

  "RoleServiceTest tests" - {
    "createRole" - {
      "should create role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)

        (dao.create _).expects(role1).returns(Future.successful(role2))

        srv.create(CreateRequest(role1)).futureValue shouldBe CreateResponse(role2)
      }
    }

    "getRole" - {
      "should return role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(Some(role1)))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.Found(role1)
      }

      "should return NotFound on unknown role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(None))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.NotFound(id)
      }
    }

    "updateRole" - {
      "should update existing role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(role1).returns(Future.successful(Some(role2)))

        srv.update(UpdateRequest(role1)).futureValue shouldBe UpdateResponse.Updated(
          role2
        )
      }

      "should return NotFound on unknown role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(role1).returns(Future.successful(None))

        srv.update(UpdateRequest(role1)).futureValue shouldBe UpdateResponse.NotFound(
          role1.id.get
        )
      }

      "should return CantUpdateRoleWithoutId on role without id" in {
        val dao  = mock[Dao[Role]]
        val srv  = new ServiceImpl(dao)
        val role = role1.copy(id = None)

        srv
          .update(UpdateRequest(role))
          .futureValue shouldBe UpdateResponse.CantUpdateWithoutId
      }
    }

    "deleteRole" - {
      "should delete role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(Some(role1)))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.Deleted(role1)
      }

      "should return NotFound on unknown role" in {
        val dao = mock[Dao[Role]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(None))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.NotFound(id)
      }
    }
  }
}

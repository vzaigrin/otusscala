package ru.otus.sc.service.impl

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.RoleDao
import ru.otus.sc.model.role.Role
import ru.otus.sc.model.role._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RoleServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {

  val role1: Role = Role(Some(UUID.randomUUID()), "Name1")
  val role2: Role = Role(Some(UUID.randomUUID()), "Name2")

  "RoleServiceTest tests" - {
    "createRole" - {
      "should create role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)

        (dao.createRole _).expects(role1).returns(Future.successful(role2))

        srv.createRole(CreateRoleRequest(role1)).futureValue shouldBe CreateRoleResponse(role2)
      }
    }

    "getRole" - {
      "should return role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getRole _).expects(id).returns(Future.successful(Some(role1)))

        srv.getRole(GetRoleRequest(id)).futureValue shouldBe GetRoleResponse.Found(role1)
      }

      "should return NotFound on unknown role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getRole _).expects(id).returns(Future.successful(None))

        srv.getRole(GetRoleRequest(id)).futureValue shouldBe GetRoleResponse.NotFound(id)
      }
    }

    "updateRole" - {
      "should update existing role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)

        (dao.updateRole _).expects(role1).returns(Future.successful(Some(role2)))

        srv.updateRole(UpdateRoleRequest(role1)).futureValue shouldBe UpdateRoleResponse.Updated(
          role2
        )
      }

      "should return NotFound on unknown role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)

        (dao.updateRole _).expects(role1).returns(Future.successful(None))

        srv.updateRole(UpdateRoleRequest(role1)).futureValue shouldBe UpdateRoleResponse.NotFound(
          role1.id.get
        )
      }

      "should return CantUpdateRoleWithoutId on role without id" in {
        val dao  = mock[RoleDao]
        val srv  = new RoleServiceImpl(dao)
        val role = role1.copy(id = None)

        srv
          .updateRole(UpdateRoleRequest(role))
          .futureValue shouldBe UpdateRoleResponse.CantUpdateRoleWithoutId
      }
    }

    "deleteRole" - {
      "should delete role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteRole _).expects(id).returns(Future.successful(Some(role1)))

        srv.deleteRole(DeleteRoleRequest(id)).futureValue shouldBe DeleteRoleResponse.Deleted(role1)
      }

      "should return NotFound on unknown role" in {
        val dao = mock[RoleDao]
        val srv = new RoleServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteRole _).expects(id).returns(Future.successful(None))

        srv.deleteRole(DeleteRoleRequest(id)).futureValue shouldBe DeleteRoleResponse.NotFound(id)
      }
    }

    "findRoles" - {
      "by last name" - {
        "should return empty list" in {
          val dao  = mock[RoleDao]
          val srv  = new RoleServiceImpl(dao)
          val name = "abc"

          (dao.findByName _).expects(name).returns(Future.successful(Seq.empty))

          srv
            .findRole(FindRoleRequest.ByName(name))
            .futureValue shouldBe FindRoleResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao  = mock[RoleDao]
          val srv  = new RoleServiceImpl(dao)
          val name = "abc"

          (dao.findByName _).expects(name).returns(Future.successful(Seq(role1, role2)))

          srv
            .findRole(FindRoleRequest.ByName(name))
            .futureValue shouldBe FindRoleResponse.Result(
            Seq(role1, role2)
          )
        }
      }
    }
  }
}

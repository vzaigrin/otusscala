package ru.otus.sc.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.role.Role

abstract class RoleDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao(): RoleDao
  def destroyDao(): Unit

  implicit val role1: Role = Role(None, "Reader")
  implicit val role2: Role = Role(None, "Manager")
  implicit val role3: Role = Role(None, "Admin")

  name - {
    "createRole" - {
      "create one role" in {
        val dao: RoleDao      = getDao()
        val createdRole: Role = dao.createRole(role1).futureValue

        createdRole.id shouldNot be(None)
        createdRole shouldBe role1.copy(id = createdRole.id)
      }
    }

    "getRole" - {
      "get unknown role" in {
        val dao: RoleDao          = getDao()
        val gotRole: Option[Role] = dao.getRole(UUID.randomUUID()).futureValue

        gotRole shouldBe None
      }

      "get known role" in {
        val dao: RoleDao          = getDao()
        val createdRole: Role     = dao.createRole(role1).futureValue
        val gotRole: Option[Role] = dao.getRole(createdRole.id.get).futureValue

        gotRole shouldBe Some(createdRole)
      }
    }

    "updateRole" - {
      "change name" in {
        val dao: RoleDao      = getDao()
        val createdRole: Role = dao.createRole(role1).futureValue
        val updatedRole: Option[Role] =
          dao.updateRole(createdRole.copy(name = "Updated")).futureValue

        updatedRole shouldNot be(Some(createdRole))
        updatedRole shouldBe Some(createdRole.copy(name = "Updated"))
      }
    }

    "deleteRole" - {
      "delete unknown role" in {
        val dao: RoleDao = getDao()
        dao.createRole(role1).futureValue
        dao.createRole(role2).futureValue
        dao.createRole(role3).futureValue
        val deletedRole: Option[Role] = dao.deleteRole(UUID.randomUUID()).futureValue

        deletedRole shouldBe None
      }

      "delete known role" in {
        val dao: RoleDao = getDao()
        dao.createRole(role1).futureValue
        val createdRole: Role = dao.createRole(role2).futureValue
        dao.createRole(role3).futureValue
        val deletedRole: Option[Role] = dao.deleteRole(createdRole.id.get).futureValue

        deletedRole shouldBe Some(createdRole)
      }
    }

    "findRole" - {
      "findRoleByName" in {
        val dao: RoleDao = getDao()
        dao.createRole(role1).futureValue
        val createdRole: Role = dao.createRole(role2).futureValue
        dao.createRole(role3).futureValue
        val foundRole: Option[Role] = dao.findByName("Manager").futureValue.headOption

        foundRole shouldBe Some(createdRole)
      }

      "findAll" in {
        val dao: RoleDao = getDao()
        val createdRoles: Seq[Role] =
          Seq(role1, role2, role3).map(dao.createRole).map(_.futureValue)
        val foundRoles: Seq[Role] = dao.findAll().futureValue

        foundRoles.toSet shouldBe createdRoles.toSet
      }
    }
  }
}

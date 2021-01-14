package ru.otus.sc.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.Role

abstract class RoleDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao: Dao[Role]
  def destroyDao(): Unit

  val role1: Role = Role(None, "Reader")
  val role2: Role = Role(None, "Manager")
  val role3: Role = Role(None, "Admin")

  name - {
    "createRole" - {
      "create one role" in {
        val dao: Dao[Role]    = getDao
        val createdRole: Role = dao.create(role1).futureValue

        createdRole.id shouldNot be(None)
        createdRole shouldBe role1.copy(id = createdRole.id)
      }
    }

    "getRole" - {
      "get unknown role" in {
        val dao: Dao[Role]        = getDao
        val gotRole: Option[Role] = dao.get(UUID.randomUUID()).futureValue

        gotRole shouldBe None
      }

      "get known role" in {
        val dao: Dao[Role]        = getDao
        val createdRole: Role     = dao.create(role1).futureValue
        val gotRole: Option[Role] = dao.get(createdRole.id.get).futureValue

        gotRole shouldBe Some(createdRole)
      }
    }

    "updateRole" - {
      "change name" in {
        val dao: Dao[Role]            = getDao
        val createdRole: Role         = dao.create(role1).futureValue
        val updatedRole: Option[Role] = dao.update(createdRole.copy(name = "Updated")).futureValue

        updatedRole shouldNot be(Some(createdRole))
        updatedRole shouldBe Some(createdRole.copy(name = "Updated"))
      }
    }

    "deleteRole" - {
      "delete unknown role" in {
        val dao: Dao[Role] = getDao
        dao.create(role1).futureValue
        dao.create(role2).futureValue
        dao.create(role3).futureValue
        val deletedRole: Option[Role] = dao.delete(UUID.randomUUID()).futureValue

        deletedRole shouldBe None
      }

      "delete known role" in {
        val dao: Dao[Role] = getDao
        dao.create(role1).futureValue
        val createdRole: Role = dao.create(role2).futureValue
        dao.create(role3).futureValue
        val deletedRole: Option[Role] = dao.delete(createdRole.id.get).futureValue

        deletedRole shouldBe Some(createdRole)
      }
    }

    "findRole" - {
      "findRoleByName" in {
        val dao: Dao[Role] = getDao
        dao.create(role1).futureValue
        val createdRole: Role = dao.create(role2).futureValue
        dao.create(role3).futureValue
        val foundRole: Option[Role] = dao.findByField("name", "Manager").futureValue.headOption

        foundRole shouldBe Some(createdRole)
      }

      "findAll" in {
        val dao: Dao[Role] = getDao
        val createdRoles: Seq[Role] =
          Seq(role1, role2, role3).map(dao.create).map(_.futureValue)
        val foundRoles: Seq[Role] = dao.findAll().futureValue

        foundRoles.toSet shouldBe createdRoles.toSet
      }
    }
  }
}

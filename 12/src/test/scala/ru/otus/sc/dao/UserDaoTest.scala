package ru.otus.sc.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.Main.md5
import ru.otus.sc.model.role.Role
import ru.otus.sc.model.user.User

abstract class UserDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao(): UserDao
  def destroyDao(): Unit

  implicit var readerRole: Role  = _
  implicit var managerRole: Role = _
  implicit var adminRole: Role   = _

  name - {
    "createUser" - {
      "create one user" in {
        val dao: UserDao      = getDao()
        val user1: User       = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val createdUser: User = dao.createUser(user1).futureValue

        createdUser.id shouldNot be(None)
        createdUser shouldBe user1.copy(id = createdUser.id)
      }
    }

    "getUser" - {
      "get unknown user" in {
        val dao: UserDao          = getDao()
        val gotUser: Option[User] = dao.getUser(UUID.randomUUID()).futureValue

        gotUser shouldBe None
      }

      "get known user" in {
        val dao: UserDao          = getDao()
        val user1: User           = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val createdUser: User     = dao.createUser(user1).futureValue
        val gotUser: Option[User] = dao.getUser(createdUser.id.get).futureValue

        gotUser shouldBe Some(createdUser)
      }
    }

    "updateUser" - {
      "change name" in {
        val dao: UserDao      = getDao()
        val user1: User       = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val createdUser: User = dao.createUser(user1).futureValue
        val updatedUser: Option[User] =
          dao.updateUser(createdUser.copy(firstName = "Updated")).futureValue

        updatedUser shouldNot be(Some(createdUser))
        updatedUser shouldBe Some(createdUser.copy(firstName = "Updated"))
      }
    }

    "deleteUser" - {
      "delete unknown user" in {
        val dao: UserDao = getDao()
        val user1: User  = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.createUser(user1).futureValue
        dao.createUser(user2).futureValue
        dao.createUser(user3).futureValue
        val deletedUser: Option[User] = dao.deleteUser(UUID.randomUUID()).futureValue

        deletedUser shouldBe None
      }

      "delete known user" in {
        val dao: UserDao = getDao()
        val user1: User  = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.createUser(user1).futureValue
        val createdUser: User = dao.createUser(user2).futureValue
        dao.createUser(user3).futureValue
        val deletedUser: Option[User] = dao.deleteUser(createdUser.id.get).futureValue

        deletedUser shouldBe Some(createdUser)
      }
    }

    "findUser" - {
      "findByUserName" in {
        val dao: UserDao = getDao()
        val user1: User  = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.createUser(user1).futureValue
        val createdUser: User = dao.createUser(user2).futureValue
        dao.createUser(user3).futureValue
        val foundUser: Option[User] = dao.findByUserName("manager").futureValue.headOption

        foundUser shouldBe Some(createdUser)
      }

      "findByLastName" in {
        val dao: UserDao = getDao()
        val user1: User  = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.createUser(user1).futureValue
        dao.createUser(user2).futureValue
        val createdUser: User       = dao.createUser(user3).futureValue
        val foundUser: Option[User] = dao.findByLastName("Иванов").futureValue.headOption

        foundUser shouldBe Some(createdUser)
      }

      "findByRole" in {
        val dao: UserDao = getDao()
        val user1: User  = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.createUser(user1).futureValue
        val createdUser: User = dao.createUser(user2).futureValue
        dao.createUser(user3).futureValue
        val foundUser: Option[User] = dao.findByRole(managerRole).futureValue.headOption

        foundUser shouldBe Some(createdUser)
      }

      "findAll" in {
        val dao: UserDao = getDao()
        val user1: User  = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        val createdUsers: Seq[User] =
          Seq(user1, user2, user3).map(dao.createUser).map(_.futureValue)
        val foundUsers: Seq[User] = dao.findAll().futureValue

        foundUsers.toSet shouldBe createdUsers.toSet
      }
    }
  }
}

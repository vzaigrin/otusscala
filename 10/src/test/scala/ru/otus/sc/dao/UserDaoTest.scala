package ru.otus.sc.dao

import java.util.UUID
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.Main.md5
import ru.otus.sc.model.User

abstract class UserDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao: Dao[User]
  def destroyDao(): Unit

  implicit var readerRole: UUID  = UUID.randomUUID()
  implicit var managerRole: UUID = UUID.randomUUID()
  implicit var adminRole: UUID   = UUID.randomUUID()

  name - {
    "createUser" - {
      "create one user" in {
        val dao: Dao[User]    = getDao
        val user1: User       = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val createdUser: User = dao.create(user1).futureValue

        createdUser.id shouldNot be(None)
        createdUser shouldBe user1.copy(id = createdUser.id)
      }
    }

    "getUser" - {
      "get unknown user" in {
        val dao: Dao[User]        = getDao
        val gotUser: Option[User] = dao.get(UUID.randomUUID()).futureValue

        gotUser shouldBe None
      }

      "get known user" in {
        val dao: Dao[User]        = getDao
        val user1: User           = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val createdUser: User     = dao.create(user1).futureValue
        val gotUser: Option[User] = dao.get(createdUser.id.get).futureValue

        gotUser shouldBe Some(createdUser)
      }
    }

    "updateUser" - {
      "change name" in {
        val dao: Dao[User]    = getDao
        val user1: User       = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val createdUser: User = dao.create(user1).futureValue
        val updatedUser: Option[User] =
          dao.update(createdUser.copy(firstName = "Updated")).futureValue

        updatedUser shouldNot be(Some(createdUser))
        updatedUser shouldBe Some(createdUser.copy(firstName = "Updated"))
      }
    }

    "deleteUser" - {
      "delete unknown user" in {
        val dao: Dao[User] = getDao
        val user1: User    = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.create(user1).futureValue
        dao.create(user2).futureValue
        dao.create(user3).futureValue
        val deletedUser: Option[User] = dao.delete(UUID.randomUUID()).futureValue

        deletedUser shouldBe None
      }

      "delete known user" in {
        val dao: Dao[User] = getDao
        val user1: User    = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.create(user1).futureValue
        val createdUser: User = dao.create(user2).futureValue
        dao.create(user3).futureValue
        val deletedUser: Option[User] = dao.delete(createdUser.id.get).futureValue

        deletedUser shouldBe Some(createdUser)
      }
    }

    "findUser" - {
      "findByUserName" in {
        val dao: Dao[User] = getDao
        val user1: User    = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.create(user1).futureValue
        val createdUser: User = dao.create(user2).futureValue
        dao.create(user3).futureValue
        val foundUser: Option[User] = dao.findByField("username", "manager").futureValue.headOption

        foundUser shouldBe Some(createdUser)
      }

      "findByLastName" in {
        val dao: Dao[User] = getDao
        val user1: User    = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.create(user1).futureValue
        dao.create(user2).futureValue
        val createdUser: User       = dao.create(user3).futureValue
        val foundUser: Option[User] = dao.findByField("lastname", "Иванов").futureValue.headOption

        foundUser shouldBe Some(createdUser)
      }

      "findByRole" in {
        val dao: Dao[User] = getDao
        val user1: User    = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        dao.create(user1).futureValue
        val createdUser: User = dao.create(user2).futureValue
        dao.create(user3).futureValue
        val foundUser: Option[User] =
          dao.findByField("role", managerRole.toString).futureValue.headOption

        foundUser shouldBe Some(createdUser)
      }

      "findAll" in {
        val dao: Dao[User] = getDao
        val user1: User    = User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
        val user2: User =
          User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
        val user3: User =
          User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
        val createdUsers: Seq[User] =
          Seq(user1, user2, user3).map(dao.create).map(_.futureValue)
        val foundUsers: Seq[User] = dao.findAll().futureValue

        foundUsers.toSet shouldBe createdUsers.toSet
      }
    }
  }
}

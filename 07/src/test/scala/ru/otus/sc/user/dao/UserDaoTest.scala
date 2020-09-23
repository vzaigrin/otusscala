package ru.otus.sc.user.dao

import java.util.UUID

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.otus.sc.user.model.{Role, User}
import org.scalatest.matchers.should.Matchers._

/**
  * Abstract test class that should bw inherited by tests for any UserDao implementation
  */
abstract class UserDaoTest(name: String, createDao: () => UserDao)
    extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks {
  implicit val genRole: Gen[Role]             = Gen.oneOf(Role.Admin, Role.Manager, Role.Reader)
  implicit val arbitraryRole: Arbitrary[Role] = Arbitrary(genRole)

  implicit val genUser: Gen[User] = for {
    id        <- Gen.option(Gen.uuid)
    firstName <- arbitrary[String]
    lastName  <- arbitrary[String]
    age       <- arbitrary[Int]
    roles     <- arbitrary[Seq[Role]]
  } yield User(id = id, firstName = firstName, lastName = lastName, age = age, roles = roles.toSet)

  implicit val arbitraryUser: Arbitrary[User] = Arbitrary(genUser)

  name - {
    "createUser" - {
      "create any number of users" in {
        forAll { (users: Seq[User], user: User) =>
          val dao = createDao()
          users.foreach(dao.createUser)

          val createdUser = dao.createUser(user)
          createdUser.id shouldNot be(user.id)
          createdUser.id shouldNot be(None)

          createdUser shouldBe user.copy(id = createdUser.id)
        }
      }
    }

    "getUser" - {
      "get unknown user" in {
        forAll { (users: Seq[User], userId: UUID) =>
          val dao = createDao()
          users.foreach(dao.createUser)

          dao.getUser(userId) shouldBe None
        }
      }

      "get known user" in {
        forAll { (users1: Seq[User], user: User, users2: Seq[User]) =>
          val dao = createDao()
          users1.foreach(dao.createUser)
          val createdUser = dao.createUser(user)
          users2.foreach(dao.createUser)

          dao.getUser(createdUser.id.get) shouldBe Some(createdUser)
        }
      }
    }

    "updateUser" - {
      "update unknown user - keep all users the same" in {
        forAll { (users: Seq[User], user: User) =>
          val dao          = createDao()
          val createdUsers = users.map(dao.createUser)

          dao.updateUser(user) shouldBe None

          createdUsers.foreach { u =>
            dao.getUser(u.id.get) shouldBe Some(u)
          }
        }
      }

      "update known user - keep other users the same" in {
        forAll { (users1: Seq[User], user1: User, user2: User, users2: Seq[User]) =>
          val dao           = createDao()
          val createdUsers1 = users1.map(dao.createUser)
          val createdUser   = dao.createUser(user1)
          val createdUsers2 = users2.map(dao.createUser)

          val toUpdate = user2.copy(id = createdUser.id)
          dao.updateUser(toUpdate) shouldBe Some(toUpdate)
          dao.getUser(toUpdate.id.get) shouldBe Some(toUpdate)

          createdUsers1.foreach { u =>
            dao.getUser(u.id.get) shouldBe Some(u)
          }

          createdUsers2.foreach { u =>
            dao.getUser(u.id.get) shouldBe Some(u)
          }
        }
      }
    }

    "deleteUser" - {
      "delete unknown user - keep all users the same" in {
        forAll { (users: Seq[User], userId: UUID) =>
          val dao          = createDao()
          val createdUsers = users.map(dao.createUser)

          dao.deleteUser(userId) shouldBe None

          createdUsers.foreach { u =>
            dao.getUser(u.id.get) shouldBe Some(u)
          }
        }
      }

      "delete known user - keep other users the same" in {
        forAll { (users1: Seq[User], user1: User, users2: Seq[User]) =>
          val dao           = createDao()
          val createdUsers1 = users1.map(dao.createUser)
          val createdUser   = dao.createUser(user1)
          val createdUsers2 = users2.map(dao.createUser)

          dao.getUser(createdUser.id.get) shouldBe Some(createdUser)
          dao.deleteUser(createdUser.id.get) shouldBe Some(createdUser)
          dao.getUser(createdUser.id.get) shouldBe None

          createdUsers1.foreach { u =>
            dao.getUser(u.id.get) shouldBe Some(u)
          }

          createdUsers2.foreach { u =>
            dao.getUser(u.id.get) shouldBe Some(u)
          }
        }
      }

    }

    "findByLastName" in {
      forAll { (users1: Seq[User], lastName: String, users2: Seq[User]) =>
        val dao               = createDao()
        val withOtherLastName = users1.filterNot(_.lastName == lastName)
        val withLastName      = users2.map(_.copy(lastName = lastName))

        withOtherLastName.foreach(dao.createUser)
        val createdWithLasName = withLastName.map(dao.createUser)

        dao.findByLastName(lastName).toSet shouldBe createdWithLasName.toSet
      }
    }

    "findAll" in {
      forAll { users: Seq[User] =>
        val dao          = createDao()
        val createdUsers = users.map(dao.createUser)

        dao.findAll().toSet shouldBe createdUsers.toSet
      }
    }
  }
}

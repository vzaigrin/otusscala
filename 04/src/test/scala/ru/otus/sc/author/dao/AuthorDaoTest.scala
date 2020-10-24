package ru.otus.sc.author.dao

import java.util.UUID
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.author.model.Author

abstract class AuthorDaoTest(name: String, createDao: () => AuthorDao)
    extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks {
  implicit val genAuthor: Gen[Author] = for {
    id        <- Gen.option(Gen.uuid)
    firstName <- arbitrary[String]
    lastName  <- arbitrary[String]
  } yield Author(id = id, firstName = firstName, lastName = lastName)

  implicit val arbitraryAuthor: Arbitrary[Author] = Arbitrary(genAuthor)

  name - {
    "createAuthor" - {
      "create any number of authors" in {
        forAll { (authors: Seq[Author], author: Author) =>
          val dao = createDao()
          authors.foreach(dao.createAuthor)

          val createdAuthor = dao.createAuthor(author)
          createdAuthor.id shouldNot be(author.id)
          createdAuthor.id shouldNot be(None)

          createdAuthor shouldBe author.copy(id = createdAuthor.id)
        }
      }
    }

    "getAuthor" - {
      "get unknown author" in {
        forAll { (authors: Seq[Author], authorId: UUID) =>
          val dao = createDao()
          authors.foreach(dao.createAuthor)

          dao.getAuthor(authorId) shouldBe None
        }
      }

      "get known author" in {
        forAll { (authors1: Seq[Author], author: Author, authors2: Seq[Author]) =>
          val dao = createDao()
          authors1.foreach(dao.createAuthor)
          val createdAuthor = dao.createAuthor(author)
          authors2.foreach(dao.createAuthor)

          dao.getAuthor(createdAuthor.id.get) shouldBe Some(createdAuthor)
        }
      }
    }

    "updateAuthor" - {
      "update unknown author - keep all authors the same" in {
        forAll { (authors: Seq[Author], author: Author) =>
          val dao            = createDao()
          val createdAuthors = authors.map(dao.createAuthor)

          dao.updateAuthor(author) shouldBe None

          createdAuthors.foreach { u =>
            dao.getAuthor(u.id.get) shouldBe Some(u)
          }
        }
      }

      "update known author - keep other authors the same" in {
        forAll { (authors1: Seq[Author], author1: Author, author2: Author, authors2: Seq[Author]) =>
          val dao             = createDao()
          val createdAuthors1 = authors1.map(dao.createAuthor)
          val createdAuthor   = dao.createAuthor(author1)
          val createdAuthors2 = authors2.map(dao.createAuthor)

          val toUpdate = author2.copy(id = createdAuthor.id)
          dao.updateAuthor(toUpdate) shouldBe Some(toUpdate)
          dao.getAuthor(toUpdate.id.get) shouldBe Some(toUpdate)

          createdAuthors1.foreach { u =>
            dao.getAuthor(u.id.get) shouldBe Some(u)
          }

          createdAuthors2.foreach { u =>
            dao.getAuthor(u.id.get) shouldBe Some(u)
          }
        }
      }
    }

    "deleteAuthor" - {
      "delete unknown author - keep all authors the same" in {
        forAll { (authors: Seq[Author], authorId: UUID) =>
          val dao            = createDao()
          val createdAuthors = authors.map(dao.createAuthor)

          dao.deleteAuthor(authorId) shouldBe None

          createdAuthors.foreach { u =>
            dao.getAuthor(u.id.get) shouldBe Some(u)
          }
        }
      }

      "delete known author - keep other authors the same" in {
        forAll { (authors1: Seq[Author], author1: Author, authors2: Seq[Author]) =>
          val dao             = createDao()
          val createdAuthors1 = authors1.map(dao.createAuthor)
          val createdAuthor   = dao.createAuthor(author1)
          val createdAuthors2 = authors2.map(dao.createAuthor)

          dao.getAuthor(createdAuthor.id.get) shouldBe Some(createdAuthor)
          dao.deleteAuthor(createdAuthor.id.get) shouldBe Some(createdAuthor)
          dao.getAuthor(createdAuthor.id.get) shouldBe None

          createdAuthors1.foreach { u =>
            dao.getAuthor(u.id.get) shouldBe Some(u)
          }

          createdAuthors2.foreach { u =>
            dao.getAuthor(u.id.get) shouldBe Some(u)
          }
        }
      }

    }

    "findByLastName" in {
      forAll { (authors1: Seq[Author], lastName: String, authors2: Seq[Author]) =>
        val dao               = createDao()
        val withOtherLastName = authors1.filterNot(_.lastName == lastName)
        val withLastName      = authors2.map(_.copy(lastName = lastName))

        withOtherLastName.foreach(dao.createAuthor)
        val createdWithLasName = withLastName.map(dao.createAuthor)

        dao.findByLastName(lastName).toSet shouldBe createdWithLasName.toSet
      }
    }

    "findAll" in {
      forAll { authors: Seq[Author] =>
        val dao            = createDao()
        val createdAuthors = authors.map(dao.createAuthor)

        dao.findAll().toSet shouldBe createdAuthors.toSet
      }
    }
  }
}

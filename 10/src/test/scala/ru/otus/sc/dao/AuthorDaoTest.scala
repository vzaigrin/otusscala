package ru.otus.sc.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.author.Author

abstract class AuthorDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao(): AuthorDao
  def destroyDao(): Unit

  implicit val author1: Author = Author(None, "Лев", "Толстой")
  implicit val author2: Author = Author(None, "Фёдор", "Достоевский")
  implicit val author3: Author = Author(None, "Николай", "Гоголь")

  name - {
    "createAuthor" - {
      "create one author" in {
        val dao: AuthorDao        = getDao()
        val createdAuthor: Author = dao.createAuthor(author1).futureValue

        createdAuthor.id shouldNot be(None)
        createdAuthor shouldBe author1.copy(id = createdAuthor.id)
      }
    }

    "getAuthor" - {
      "get unknown author" in {
        val dao: AuthorDao            = getDao()
        val gotAuthor: Option[Author] = dao.getAuthor(UUID.randomUUID()).futureValue

        gotAuthor shouldBe None
      }

      "get known author" in {
        val dao: AuthorDao            = getDao()
        val createdAuthor: Author     = dao.createAuthor(author1).futureValue
        val gotAuthor: Option[Author] = dao.getAuthor(createdAuthor.id.get).futureValue

        gotAuthor shouldBe Some(createdAuthor)
      }
    }

    "updateAuthor" - {
      "change name" in {
        val dao: AuthorDao        = getDao()
        val createdAuthor: Author = dao.createAuthor(author1).futureValue
        val updatedAuthor: Option[Author] =
          dao.updateAuthor(createdAuthor.copy(firstName = "Updated")).futureValue

        updatedAuthor shouldNot be(Some(createdAuthor))
        updatedAuthor shouldBe Some(createdAuthor.copy(firstName = "Updated"))
      }
    }

    "deleteAuthor" - {
      "delete unknown author" in {
        val dao: AuthorDao = getDao()
        dao.createAuthor(author1).futureValue
        dao.createAuthor(author2).futureValue
        dao.createAuthor(author3).futureValue
        val deletedAuthor: Option[Author] = dao.deleteAuthor(UUID.randomUUID()).futureValue

        deletedAuthor shouldBe None
      }

      "delete known author" in {
        val dao: AuthorDao = getDao()
        dao.createAuthor(author1).futureValue
        val createdAuthor: Author = dao.createAuthor(author2).futureValue
        dao.createAuthor(author3).futureValue
        val deletedAuthor: Option[Author] = dao.deleteAuthor(createdAuthor.id.get).futureValue

        deletedAuthor shouldBe Some(createdAuthor)
      }
    }

    "findAuthor" - {
      "findByLastName" in {
        val dao: AuthorDao = getDao()
        dao.createAuthor(author1).futureValue
        val createdAuthor: Author = dao.createAuthor(author2).futureValue
        dao.createAuthor(author3).futureValue
        val foundAuthor: Option[Author] = dao.findByLastName("Достоевский").futureValue.headOption

        foundAuthor shouldBe Some(createdAuthor)
      }

      "findAll" in {
        val dao: AuthorDao = getDao()
        val createdAuthors: Seq[Author] =
          Seq(author1, author2, author3).map(dao.createAuthor).map(_.futureValue)
        val foundAuthors: Seq[Author] = dao.findAll().futureValue

        foundAuthors.toSet shouldBe createdAuthors.toSet
      }
    }
  }
}

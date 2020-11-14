package ru.otus.sc.dao

import java.util.UUID
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.Book
import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class BookDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao: Dao[Book]
  def destroyDao(): Unit

  val tolstoy: UUID    = UUID.randomUUID()
  val dostoevsky: UUID = UUID.randomUUID()
  val gogol: UUID      = UUID.randomUUID()
  val pushkin: UUID    = UUID.randomUUID()
  val lermontov: UUID  = UUID.randomUUID()

  name - {
    "createBook" - {
      "create one book" in {
        val dao: Dao[Book]    = getDao
        val book1             = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val createdBook: Book = Await.result(dao.create(book1), Duration.Inf)

        createdBook.id shouldNot be(None)
        createdBook shouldBe book1.copy(id = createdBook.id)
      }
    }

    "getBook" - {
      "get unknown book" in {
        val dao: Dao[Book]        = getDao
        val gotBook: Option[Book] = dao.get(UUID.randomUUID()).futureValue

        gotBook shouldBe None
      }

      "get known book" in {
        val dao: Dao[Book] = getDao
        val createdBook: Book =
          dao
            .create(Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300))
            .futureValue
        val gotBook: Option[Book] = dao.get(createdBook.id.get).futureValue

        gotBook shouldBe Some(createdBook)
      }
    }

    "updateBook" - {
      "change name" in {
        val dao: Dao[Book] = getDao
        val createdBook: Book =
          dao
            .create(Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300))
            .futureValue
        val updatedBook: Option[Book] =
          dao.update(createdBook.copy(title = "Updated")).futureValue

        updatedBook shouldNot be(Some(createdBook))
        updatedBook shouldBe Some(createdBook.copy(title = "Updated"))
      }
    }

    "deleteBook" - {
      "delete unknown book" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.create(book1).futureValue
        dao.create(book2).futureValue
        dao.create(book3).futureValue
        val deletedBook: Option[Book] = dao.delete(UUID.randomUUID()).futureValue

        deletedBook shouldBe None
      }

      "delete known book" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.create(book1).futureValue
        val createdBook: Book = dao.create(book2).futureValue
        dao.create(book3).futureValue
        val deletedBook: Option[Book] = dao.delete(createdBook.id.get).futureValue

        deletedBook shouldBe Some(createdBook)
      }
    }

    "findBook" - {
      "findByTitle" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.create(book1).futureValue
        val createdBook: Book = dao.create(book2).futureValue
        dao.create(book3).futureValue
        val foundBook: Option[Book] =
          dao.findByField("title", "Великие поэмы").futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findByAuthor" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.create(book1).futureValue
        dao.create(book2).futureValue
        val createdBook: Book = dao.create(book3).futureValue
        val foundBook: Option[Book] =
          dao.findByField("author", gogol.toString).futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findByYear" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        val createdBook: Book = dao.create(book1).futureValue
        dao.create(book2).futureValue
        dao.create(book3).futureValue
        val foundBook: Option[Book] = dao.findByField("published", "1914").futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findByPages" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.create(book1).futureValue
        dao.create(book2).futureValue
        val createdBook: Book       = dao.create(book3).futureValue
        val foundBook: Option[Book] = dao.findByField("pages", "320").futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findAll" in {
        val dao: Dao[Book] = getDao
        val book1: Book    = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book    = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book    = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        val createdBooks: Seq[Book] =
          Seq(book1, book2, book3).map(dao.create).map(_.futureValue)
        val foundBooks: Seq[Book] = dao.findAll().futureValue

        foundBooks.toSet shouldBe createdBooks.toSet
      }
    }
  }
}

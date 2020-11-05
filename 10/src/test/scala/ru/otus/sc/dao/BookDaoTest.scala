package ru.otus.sc.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.author.Author
import ru.otus.sc.model.book.Book

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class BookDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao(): BookDao
  def destroyDao(): Unit

  implicit var tolstoy: Author    = _
  implicit var dostoevsky: Author = _
  implicit var gogol: Author      = _
  implicit var pushkin: Author    = _
  implicit var lermontov: Author  = _

  name - {
    "createBook" - {
      "create one book" in {
        val dao: BookDao      = getDao()
        val book1             = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val createdBook: Book = Await.result(dao.createBook(book1), Duration.Inf)

        createdBook.id shouldNot be(None)
        createdBook shouldBe book1.copy(id = createdBook.id)
      }
    }

    "getBook" - {
      "get unknown book" in {
        val dao: BookDao          = getDao()
        val gotBook: Option[Book] = dao.getBook(UUID.randomUUID()).futureValue

        gotBook shouldBe None
      }

      "get known book" in {
        val dao: BookDao = getDao()
        val createdBook: Book =
          dao
            .createBook(Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300))
            .futureValue
        val gotBook: Option[Book] = dao.getBook(createdBook.id.get).futureValue

        gotBook shouldBe Some(createdBook)
      }
    }

    "updateBook" - {
      "change name" in {
        val dao: BookDao = getDao()
        val createdBook: Book =
          dao
            .createBook(Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300))
            .futureValue
        val updatedBook: Option[Book] =
          dao.updateBook(createdBook.copy(title = "Updated")).futureValue

        updatedBook shouldNot be(Some(createdBook))
        updatedBook shouldBe Some(createdBook.copy(title = "Updated"))
      }
    }

    "deleteBook" - {
      "delete unknown book" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.createBook(book1).futureValue
        dao.createBook(book2).futureValue
        dao.createBook(book3).futureValue
        val deletedBook: Option[Book] = dao.deleteBook(UUID.randomUUID()).futureValue

        deletedBook shouldBe None
      }

      "delete known book" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.createBook(book1).futureValue
        val createdBook: Book = dao.createBook(book2).futureValue
        dao.createBook(book3).futureValue
        val deletedBook: Option[Book] = dao.deleteBook(createdBook.id.get).futureValue

        deletedBook shouldBe Some(createdBook)
      }
    }

    "findBook" - {
      "findByTitle" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.createBook(book1).futureValue
        val createdBook: Book = dao.createBook(book2).futureValue
        dao.createBook(book3).futureValue
        val foundBook: Option[Book] = dao.findByTitle("Великие поэмы").futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findByAuthor" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.createBook(book1).futureValue
        dao.createBook(book2).futureValue
        val createdBook: Book       = dao.createBook(book3).futureValue
        val foundBook: Option[Book] = dao.findByAuthor(gogol).futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findByYear" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        val createdBook: Book = dao.createBook(book1).futureValue
        dao.createBook(book2).futureValue
        dao.createBook(book3).futureValue
        val foundBook: Option[Book] = dao.findByYear(1914).futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findByPages" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        dao.createBook(book1).futureValue
        dao.createBook(book2).futureValue
        val createdBook: Book       = dao.createBook(book3).futureValue
        val foundBook: Option[Book] = dao.findByPages(320).futureValue.headOption

        foundBook shouldBe Some(createdBook)
      }

      "findAll" in {
        val dao: BookDao = getDao()
        val book1: Book  = Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)
        val book2: Book  = Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)
        val book3: Book  = Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)

        val createdBooks: Seq[Book] =
          Seq(book1, book2, book3).map(dao.createBook).map(_.futureValue)
        val foundBooks: Seq[Book] = dao.findAll().futureValue

        foundBooks.toSet shouldBe createdBooks.toSet
      }
    }
  }
}

package ru.otus.sc.book.service.impl

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.author.model.Author
import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model._

class BookServiceImplTest extends AnyFreeSpec with MockFactory {
  val tolstoy: UUID    = Author(Some(UUID.randomUUID()), "Leo", "Tolstoy").id.get
  val dostoevsky: UUID = Author(Some(UUID.randomUUID()), "Fyodor", "Dostoevsky").id.get

  val book1: Book = Book(Some(UUID.randomUUID()), "War and Peace", Set(tolstoy), 1869, 1274)
  val book2: Book =
    Book(Some(UUID.randomUUID()), "Crime and Punishment", Set(dostoevsky), 1866, 672)

  "BookServiceTest tests" - {
    "createBook" - {
      "should create book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        (dao.createBook _).expects(book1).returns(book2)

        srv.createBook(CreateBookRequest(book1)) shouldBe CreateBookResponse(book2)
      }
    }

    "getBook" - {
      "should return book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getBook _).expects(id).returns(Some(book1))

        srv.getBook(GetBookRequest(id)) shouldBe GetBookResponse.Found(book1)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getBook _).expects(id).returns(None)

        srv.getBook(GetBookRequest(id)) shouldBe GetBookResponse.NotFound(id)
      }
    }

    "updateBook" - {
      "should update existing book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        (dao.updateBook _).expects(book1).returns(Some(book2))

        srv.updateBook(UpdateBookRequest(book1)) shouldBe UpdateBookResponse.Updated(book2)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        (dao.updateBook _).expects(book1).returns(None)

        srv.updateBook(UpdateBookRequest(book1)) shouldBe UpdateBookResponse.NotFound(book1.id.get)
      }

      "should return CantUpdateBookWithoutId on book without id" in {
        val dao  = mock[BookDao]
        val srv  = new BookServiceImpl(dao)
        val book = book1.copy(id = None)

        srv.updateBook(UpdateBookRequest(book)) shouldBe UpdateBookResponse.CantUpdateBookWithoutId
      }
    }

    "deleteBook" - {
      "should delete book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteBook _).expects(id).returns(Some(book1))

        srv.deleteBook(DeleteBookRequest(id)) shouldBe DeleteBookResponse.Deleted(book1)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteBook _).expects(id).returns(None)

        srv.deleteBook(DeleteBookRequest(id)) shouldBe DeleteBookResponse.NotFound(id)
      }
    }

    "findBooks" - {
      "by title" - {
        "should return empty list" in {
          val dao   = mock[BookDao]
          val srv   = new BookServiceImpl(dao)
          val title = "abc"

          (dao.findByTitle _).expects(title).returns(Seq.empty)

          srv.findBooks(FindBooksRequest.ByTitle(title)) shouldBe FindBooksResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao   = mock[BookDao]
          val srv   = new BookServiceImpl(dao)
          val title = "abc"

          (dao.findByTitle _).expects(title).returns(Seq(book1, book2))

          srv.findBooks(FindBooksRequest.ByTitle(title)) shouldBe FindBooksResponse.Result(
            Seq(book1, book2)
          )
        }
      }

      "by author" - {
        "should return empty list" in {
          val dao = mock[BookDao]
          val srv = new BookServiceImpl(dao)
          val id  = UUID.randomUUID()

          (dao.findByAuthor _).expects(id).returns(Seq.empty)

          srv.findBooks(FindBooksRequest.ByAuthor(id)) shouldBe FindBooksResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao = mock[BookDao]
          val srv = new BookServiceImpl(dao)
          val id  = UUID.randomUUID()

          (dao.findByAuthor _).expects(id).returns(Seq(book1, book2))

          srv.findBooks(FindBooksRequest.ByAuthor(id)) shouldBe FindBooksResponse.Result(
            Seq(book1, book2)
          )
        }
      }

      "by year" - {
        "should return empty list" in {
          val dao  = mock[BookDao]
          val srv  = new BookServiceImpl(dao)
          val year = 1111

          (dao.findByPages _).expects(year).returns(Seq.empty)

          srv.findBooks(FindBooksRequest.ByPages(year)) shouldBe FindBooksResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao  = mock[BookDao]
          val srv  = new BookServiceImpl(dao)
          val year = 1111

          (dao.findByPages _).expects(year).returns(Seq(book1, book2))

          srv.findBooks(FindBooksRequest.ByPages(year)) shouldBe FindBooksResponse.Result(
            Seq(book1, book2)
          )
        }
      }

      "by pages" - {
        "should return empty list" in {
          val dao   = mock[BookDao]
          val srv   = new BookServiceImpl(dao)
          val pages = 555

          (dao.findByPages _).expects(pages).returns(Seq.empty)

          srv.findBooks(FindBooksRequest.ByPages(pages)) shouldBe FindBooksResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao   = mock[BookDao]
          val srv   = new BookServiceImpl(dao)
          val pages = 555

          (dao.findByPages _).expects(pages).returns(Seq(book1, book2))

          srv.findBooks(FindBooksRequest.ByPages(pages)) shouldBe FindBooksResponse.Result(
            Seq(book1, book2)
          )
        }
      }
    }
  }
}

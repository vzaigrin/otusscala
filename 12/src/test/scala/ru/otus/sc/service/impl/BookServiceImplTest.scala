package ru.otus.sc.service.impl

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.BookDao
import ru.otus.sc.model.author.Author
import ru.otus.sc.model.book._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val author1: Author = Author(Some(UUID.randomUUID()), "FirstName1", "LastName1")
  val author2: Author = Author(Some(UUID.randomUUID()), "FirstName2", "LastName2")

  val book1: Book = Book(Some(UUID.randomUUID()), "book1", Set(author1), 1, 2)
  val book2: Book = Book(Some(UUID.randomUUID()), "book2", Set(author2), 3, 4)

  "BookServiceTest tests" - {
    "createBook" - {
      "should create book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        (dao.createBook _).expects(book1).returns(Future.successful(book2))

        srv.createBook(CreateBookRequest(book1)).futureValue shouldBe CreateBookResponse(book2)
      }
    }

    "getBook" - {
      "should return book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getBook _).expects(id).returns(Future.successful(Some(book1)))

        srv.getBook(GetBookRequest(id)).futureValue shouldBe GetBookResponse.Found(book1)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getBook _).expects(id).returns(Future.successful(None))

        srv.getBook(GetBookRequest(id)).futureValue shouldBe GetBookResponse.NotFound(id)
      }
    }

    "updateBook" - {
      "should update existing book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        (dao.updateBook _).expects(book1).returns(Future.successful(Some(book2)))

        srv.updateBook(UpdateBookRequest(book1)).futureValue shouldBe UpdateBookResponse.Updated(
          book2
        )
      }

      "should return NotFound on unknown book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        (dao.updateBook _).expects(book1).returns(Future.successful(None))

        srv.updateBook(UpdateBookRequest(book1)).futureValue shouldBe UpdateBookResponse.NotFound(
          book1.id.get
        )
      }

      "should return CantUpdateBookWithoutId on book without id" in {
        val dao  = mock[BookDao]
        val srv  = new BookServiceImpl(dao)
        val book = book1.copy(id = None)

        srv
          .updateBook(UpdateBookRequest(book))
          .futureValue shouldBe UpdateBookResponse.CantUpdateBookWithoutId
      }
    }

    "deleteBook" - {
      "should delete book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteBook _).expects(id).returns(Future.successful(Some(book1)))

        srv.deleteBook(DeleteBookRequest(id)).futureValue shouldBe DeleteBookResponse.Deleted(book1)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteBook _).expects(id).returns(Future.successful(None))

        srv.deleteBook(DeleteBookRequest(id)).futureValue shouldBe DeleteBookResponse.NotFound(id)
      }
    }

    "findBooks" - {
      "by title" - {
        "should return empty list" in {
          val dao   = mock[BookDao]
          val srv   = new BookServiceImpl(dao)
          val title = "abc"

          (dao.findByTitle _).expects(title).returns(Future.successful(Seq.empty))

          srv
            .findBook(FindBookRequest.ByTitle(title))
            .futureValue shouldBe FindBookResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao   = mock[BookDao]
          val srv   = new BookServiceImpl(dao)
          val title = "abc"

          (dao.findByTitle _).expects(title).returns(Future.successful(Seq(book1, book2)))

          srv
            .findBook(FindBookRequest.ByTitle(title))
            .futureValue shouldBe FindBookResponse.Result(
            Seq(book1, book2)
          )
        }
      }
    }
  }
}

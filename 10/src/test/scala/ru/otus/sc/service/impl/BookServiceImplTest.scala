package ru.otus.sc.service.impl

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.Dao
import ru.otus.sc.model._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val author1: UUID = UUID.randomUUID()
  val author2: UUID = UUID.randomUUID()

  val book1: Book = Book(Some(UUID.randomUUID()), "book1", Set(author1), 1, 2)
  val book2: Book = Book(Some(UUID.randomUUID()), "book2", Set(author2), 3, 4)

  "BookServiceTest tests" - {
    "createBook" - {
      "should create book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)

        (dao.create _).expects(book1).returns(Future.successful(book2))

        srv.create(CreateRequest(book1)).futureValue shouldBe CreateResponse(book2)
      }
    }

    "getBook" - {
      "should return book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(Some(book1)))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.Found(book1)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(None))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.NotFound(id)
      }
    }

    "updateBook" - {
      "should update existing book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(book1).returns(Future.successful(Some(book2)))

        srv.update(UpdateRequest(book1)).futureValue shouldBe UpdateResponse.Updated(
          book2
        )
      }

      "should return NotFound on unknown book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(book1).returns(Future.successful(None))

        srv.update(UpdateRequest(book1)).futureValue shouldBe UpdateResponse.NotFound(
          book1.id.get
        )
      }

      "should return CantUpdateBookWithoutId on book without id" in {
        val dao  = mock[Dao[Book]]
        val srv  = new ServiceImpl(dao)
        val book = book1.copy(id = None)

        srv
          .update(UpdateRequest(book))
          .futureValue shouldBe UpdateResponse.CantUpdateWithoutId
      }
    }

    "deleteBook" - {
      "should delete book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(Some(book1)))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.Deleted(book1)
      }

      "should return NotFound on unknown book" in {
        val dao = mock[Dao[Book]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(None))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.NotFound(id)
      }
    }
  }
}

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

class AuthorServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val author1: Author = Author(Some(UUID.randomUUID()), "FirstName1", "LastName1")
  val author2: Author = Author(Some(UUID.randomUUID()), "FirstName2", "LastName2")

  "AuthorServiceTest tests" - {
    "createAuthor" - {
      "should create author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)

        (dao.create _).expects(author1).returns(Future.successful(author2))
        srv.create(CreateRequest(author1)).futureValue shouldBe CreateResponse(author2)
      }
    }

    "getAuthor" - {
      "should return author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(Some(author1)))
        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.Found(author1)
      }

      "should return NotFound on unknown author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(None))
        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.NotFound(id)
      }
    }

    "updateAuthor" - {
      "should update existing author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(author1).returns(Future.successful(Some(author2)))
        srv.update(UpdateRequest(author1)).futureValue shouldBe UpdateResponse.Updated(author2)
      }

      "should return NotFound on unknown author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(author1).returns(Future.successful(None))
        srv.update(UpdateRequest(author1)).futureValue shouldBe UpdateResponse.NotFound(
          author1.id.get
        )
      }

      "should return CantUpdateAuthorWithoutId on author without id" in {
        val dao    = mock[Dao[Author]]
        val srv    = new ServiceImpl(dao)
        val author = author1.copy(id = None)

        srv.update(UpdateRequest(author)).futureValue shouldBe UpdateResponse.CantUpdateWithoutId
      }
    }

    "deleteAuthor" - {
      "should delete author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(Some(author1)))
        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.Deleted(author1)
      }

      "should return NotFound on unknown author" in {
        val dao = mock[Dao[Author]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(None))
        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.NotFound(id)
      }
    }
  }
}

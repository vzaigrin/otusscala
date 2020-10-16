package ru.otus.sc.service.impl

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.AuthorDao
import ru.otus.sc.model.author._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val author1: Author = Author(Some(UUID.randomUUID()), "FirstName1", "LastName1")
  val author2: Author = Author(Some(UUID.randomUUID()), "FirstName2", "LastName2")

  "AuthorServiceTest tests" - {
    "createAuthor" - {
      "should create author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)

        (dao.createAuthor _).expects(author1).returns(Future.successful(author2))

        srv.createAuthor(CreateAuthorRequest(author1)).futureValue shouldBe CreateAuthorResponse(
          author2
        )
      }
    }

    "getAuthor" - {
      "should return author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getAuthor _).expects(id).returns(Future.successful(Some(author1)))

        srv.getAuthor(GetAuthorRequest(id)).futureValue shouldBe GetAuthorResponse.Found(author1)
      }

      "should return NotFound on unknown author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getAuthor _).expects(id).returns(Future.successful(None))

        srv.getAuthor(GetAuthorRequest(id)).futureValue shouldBe GetAuthorResponse.NotFound(id)
      }
    }

    "updateAuthor" - {
      "should update existing author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)

        (dao.updateAuthor _).expects(author1).returns(Future.successful(Some(author2)))

        srv.updateAuthor(UpdateAuthorRequest(author1)).futureValue shouldBe UpdateAuthorResponse
          .Updated(
            author2
          )
      }

      "should return NotFound on unknown author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)

        (dao.updateAuthor _).expects(author1).returns(Future.successful(None))

        srv.updateAuthor(UpdateAuthorRequest(author1)).futureValue shouldBe UpdateAuthorResponse
          .NotFound(
            author1.id.get
          )
      }

      "should return CantUpdateAuthorWithoutId on author without id" in {
        val dao    = mock[AuthorDao]
        val srv    = new AuthorServiceImpl(dao)
        val author = author1.copy(id = None)

        srv
          .updateAuthor(UpdateAuthorRequest(author))
          .futureValue shouldBe UpdateAuthorResponse.CantUpdateAuthorWithoutId
      }
    }

    "deleteAuthor" - {
      "should delete author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteAuthor _).expects(id).returns(Future.successful(Some(author1)))

        srv.deleteAuthor(DeleteAuthorRequest(id)).futureValue shouldBe DeleteAuthorResponse.Deleted(
          author1
        )
      }

      "should return NotFound on unknown author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteAuthor _).expects(id).returns(Future.successful(None))

        srv.deleteAuthor(DeleteAuthorRequest(id)).futureValue shouldBe DeleteAuthorResponse
          .NotFound(id)
      }
    }

    "findAuthors" - {
      "by last name" - {
        "should return empty list" in {
          val dao      = mock[AuthorDao]
          val srv      = new AuthorServiceImpl(dao)
          val lastName = "abc"

          (dao.findByLastName _).expects(lastName).returns(Future.successful(Seq.empty))

          srv
            .findAuthor(FindAuthorRequest.ByLastName(lastName))
            .futureValue shouldBe FindAuthorResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao      = mock[AuthorDao]
          val srv      = new AuthorServiceImpl(dao)
          val lastName = "abc"

          (dao.findByLastName _).expects(lastName).returns(Future.successful(Seq(author1, author2)))

          srv
            .findAuthor(FindAuthorRequest.ByLastName(lastName))
            .futureValue shouldBe FindAuthorResponse.Result(
            Seq(author1, author2)
          )
        }
      }
    }
  }
}

package ru.otus.sc.author.service.impl

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model._

class AuthorServiceImplTest extends AnyFreeSpec with MockFactory {

  val author1: Author = Author(Some(UUID.randomUUID()), "Leo", "Tolstoy")
  val author2: Author = Author(Some(UUID.randomUUID()), "Fyodor", "Dostoevsky")

  "AuthorServiceTest tests" - {
    "createAuthor" - {
      "should create author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)

        (dao.createAuthor _).expects(author1).returns(author2)

        srv.createAuthor(CreateAuthorRequest(author1)) shouldBe CreateAuthorResponse(author2)
      }
    }

    "getAuthor" - {
      "should return author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getAuthor _).expects(id).returns(Some(author1))

        srv.getAuthor(GetAuthorRequest(id)) shouldBe GetAuthorResponse.Found(author1)
      }

      "should return NotFound on unknown author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getAuthor _).expects(id).returns(None)

        srv.getAuthor(GetAuthorRequest(id)) shouldBe GetAuthorResponse.NotFound(id)
      }
    }

    "updateAuthor" - {
      "should update existing author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)

        (dao.updateAuthor _).expects(author1).returns(Some(author2))

        srv.updateAuthor(request = UpdateAuthorRequest(author1)) shouldBe UpdateAuthorResponse
          .Updated(
            author2
          )
      }

      "should return NotFound on unknown author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)

        (dao.updateAuthor _).expects(author1).returns(None)

        srv.updateAuthor(UpdateAuthorRequest(author1)) shouldBe UpdateAuthorResponse.NotFound(
          author1.id.get
        )
      }

      "should return CantUpdateAuthorWithoutId on author without id" in {
        val dao    = mock[AuthorDao]
        val srv    = new AuthorServiceImpl(dao)
        val author = author1.copy(id = None)

        srv.updateAuthor(
          UpdateAuthorRequest(author)
        ) shouldBe UpdateAuthorResponse.CantUpdateAuthorWithoutId
      }
    }

    "deleteAuthor" - {
      "should delete author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteAuthor _).expects(id).returns(Some(author1))

        srv.deleteAuthor(DeleteAuthorRequest(id)) shouldBe DeleteAuthorResponse.Deleted(author1)
      }

      "should return NotFound on unknown author" in {
        val dao = mock[AuthorDao]
        val srv = new AuthorServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteAuthor _).expects(id).returns(None)

        srv.deleteAuthor(DeleteAuthorRequest(id)) shouldBe DeleteAuthorResponse.NotFound(id)
      }
    }

    "findAuthors" - {
      "by last name" - {
        "should return empty list" in {
          val dao      = mock[AuthorDao]
          val srv      = new AuthorServiceImpl(dao)
          val lastName = "abc"

          (dao.findByLastName _).expects(lastName).returns(Seq.empty)

          srv.findAuthors(FindAuthorsRequest.ByLastName(lastName)) shouldBe FindAuthorsResponse
            .Result(
              Seq.empty
            )
        }

        "should return non-empty list" in {
          val dao      = mock[AuthorDao]
          val srv      = new AuthorServiceImpl(dao)
          val lastName = "abc"

          (dao.findByLastName _).expects(lastName).returns(Seq(author1, author2))

          srv.findAuthors(FindAuthorsRequest.ByLastName(lastName)) shouldBe FindAuthorsResponse
            .Result(
              Seq(author1, author2)
            )
        }
      }
    }
  }
}

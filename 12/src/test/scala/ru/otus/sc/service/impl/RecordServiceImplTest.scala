package ru.otus.sc.service.impl

import java.sql.Timestamp
import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.RecordDao
import ru.otus.sc.model.author.Author
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.role.Role
import ru.otus.sc.model.record._
import ru.otus.sc.model.user.User
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RecordServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val role1: Role = Role(Some(UUID.randomUUID()), "Name1")
  val role2: Role = Role(Some(UUID.randomUUID()), "Name2")
  val user1: User = User(
    Some(UUID.randomUUID()),
    "userName1",
    "password1",
    "FirstName1",
    "LastName1",
    1,
    Set(role1)
  )
  val user2: User = User(
    Some(UUID.randomUUID()),
    "userName2",
    "password2",
    "FirstName2",
    "LastName2",
    2,
    Set(role2)
  )
  val author1: Author = Author(Some(UUID.randomUUID()), "FirstName1", "LastName1")
  val author2: Author = Author(Some(UUID.randomUUID()), "FirstName2", "LastName2")
  val book1: Book     = Book(Some(UUID.randomUUID()), "book1", Set(author1), 1, 2)
  val book2: Book     = Book(Some(UUID.randomUUID()), "book2", Set(author2), 3, 4)

  val record1: Record =
    Record(Some(UUID.randomUUID()), user1, book1, new Timestamp(0), new Timestamp(1))
  val record2: Record =
    Record(Some(UUID.randomUUID()), user2, book2, new Timestamp(10), new Timestamp(20))

  "RecordServiceTest tests" - {
    "createRecord" - {
      "should create record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)

        (dao.createRecord _).expects(record1).returns(Future.successful(record2))

        srv.createRecord(CreateRecordRequest(record1)).futureValue shouldBe CreateRecordResponse(
          record2
        )
      }
    }

    "getRecord" - {
      "should return record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getRecord _).expects(id).returns(Future.successful(Some(record1)))

        srv.getRecord(GetRecordRequest(id)).futureValue shouldBe GetRecordResponse.Found(record1)
      }

      "should return NotFound on unknown record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getRecord _).expects(id).returns(Future.successful(None))

        srv.getRecord(GetRecordRequest(id)).futureValue shouldBe GetRecordResponse.NotFound(id)
      }
    }

    "updateRecord" - {
      "should update existing record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)

        (dao.updateRecord _).expects(record1).returns(Future.successful(Some(record2)))

        srv.updateRecord(UpdateRecordRequest(record1)).futureValue shouldBe UpdateRecordResponse
          .Updated(
            record2
          )
      }

      "should return NotFound on unknown record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)

        (dao.updateRecord _).expects(record1).returns(Future.successful(None))

        srv.updateRecord(UpdateRecordRequest(record1)).futureValue shouldBe UpdateRecordResponse
          .NotFound(
            record1.id.get
          )
      }

      "should return CantUpdateRecordWithoutId on record without id" in {
        val dao    = mock[RecordDao]
        val srv    = new RecordServiceImpl(dao)
        val record = record1.copy(id = None)

        srv
          .updateRecord(UpdateRecordRequest(record))
          .futureValue shouldBe UpdateRecordResponse.CantUpdateRecordWithoutId
      }
    }

    "deleteRecord" - {
      "should delete record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteRecord _).expects(id).returns(Future.successful(Some(record1)))

        srv.deleteRecord(DeleteRecordRequest(id)).futureValue shouldBe DeleteRecordResponse.Deleted(
          record1
        )
      }

      "should return NotFound on unknown record" in {
        val dao = mock[RecordDao]
        val srv = new RecordServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteRecord _).expects(id).returns(Future.successful(None))

        srv.deleteRecord(DeleteRecordRequest(id)).futureValue shouldBe DeleteRecordResponse
          .NotFound(id)
      }
    }

    "findRecords" - {
      "by book" - {
        "should return empty list" in {
          val dao  = mock[RecordDao]
          val srv  = new RecordServiceImpl(dao)
          val book = book1

          (dao.findByBook _).expects(book).returns(Future.successful(Seq.empty))

          srv
            .findRecord(FindRecordRequest.ByBook(book))
            .futureValue shouldBe FindRecordResponse.Result(
            Seq.empty
          )
        }

        "should return non-empty list" in {
          val dao  = mock[RecordDao]
          val srv  = new RecordServiceImpl(dao)
          val book = book2

          (dao.findByBook _).expects(book).returns(Future.successful(Seq(record1, record2)))

          srv
            .findRecord(FindRecordRequest.ByBook(book))
            .futureValue shouldBe FindRecordResponse.Result(
            Seq(record1, record2)
          )
        }
      }
    }
  }
}

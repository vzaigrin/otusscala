package ru.otus.sc.service.impl

import java.time.LocalDateTime
import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.dao.Dao
import ru.otus.sc.model._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RecordServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  val role1: UUID   = UUID.randomUUID()
  val role2: UUID   = UUID.randomUUID()
  val user1: UUID   = UUID.randomUUID()
  val user2: UUID   = UUID.randomUUID()
  val author1: UUID = UUID.randomUUID()
  val author2: UUID = UUID.randomUUID()
  val book1: UUID   = UUID.randomUUID()
  val book2: UUID   = UUID.randomUUID()

  val record1: Record =
    Record(Some(UUID.randomUUID()), user1, book1, LocalDateTime.now(), LocalDateTime.now())
  val record2: Record =
    Record(Some(UUID.randomUUID()), user2, book2, LocalDateTime.now(), LocalDateTime.now())

  "RecordServiceTest tests" - {
    "create" - {
      "should create record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)

        (dao.create _).expects(record1).returns(Future.successful(record2))

        srv.create(CreateRequest(record1)).futureValue shouldBe CreateResponse(
          record2
        )
      }
    }

    "getRecord" - {
      "should return record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(Some(record1)))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.Found(record1)
      }

      "should return NotFound on unknown record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.get _).expects(id).returns(Future.successful(None))

        srv.get(GetRequest(id)).futureValue shouldBe GetResponse.NotFound(id)
      }
    }

    "updateRecord" - {
      "should update existing record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(record1).returns(Future.successful(Some(record2)))

        srv.update(UpdateRequest(record1)).futureValue shouldBe UpdateResponse
          .Updated(
            record2
          )
      }

      "should return NotFound on unknown record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)

        (dao.update _).expects(record1).returns(Future.successful(None))

        srv.update(UpdateRequest(record1)).futureValue shouldBe UpdateResponse
          .NotFound(
            record1.id.get
          )
      }

      "should return CantUpdateRecordWithoutId on record without id" in {
        val dao    = mock[Dao[Record]]
        val srv    = new ServiceImpl(dao)
        val record = record1.copy(id = None)

        srv
          .update(UpdateRequest(record))
          .futureValue shouldBe UpdateResponse.CantUpdateWithoutId
      }
    }

    "deleteRecord" - {
      "should delete record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(Some(record1)))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse.Deleted(
          record1
        )
      }

      "should return NotFound on unknown record" in {
        val dao = mock[Dao[Record]]
        val srv = new ServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.delete _).expects(id).returns(Future.successful(None))

        srv.delete(DeleteRequest(id)).futureValue shouldBe DeleteResponse
          .NotFound(id)
      }
    }
  }
}

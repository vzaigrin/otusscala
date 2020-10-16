package ru.otus.sc.dao

import java.sql.Timestamp
import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.record.Record
import ru.otus.sc.model.record
import ru.otus.sc.model.user.User

abstract class RecordDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao(): RecordDao
  def destroyDao(): Unit

  implicit var reader: User  = _
  implicit var manager: User = _
  implicit var admin: User   = _
  implicit var book1: Book   = _
  implicit var book2: Book   = _
  implicit var book3: Book   = _

  name - {
    "createRecord" - {
      "create one record" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val createdRecord: Record = dao.createRecord(record1).futureValue

        createdRecord.id shouldNot be(None)
        createdRecord shouldBe record1.copy(id = createdRecord.id)
      }
    }

    "getRecord" - {
      "get unknown record" in {
        val dao: RecordDao            = getDao()
        val gotRecord: Option[Record] = dao.getRecord(UUID.randomUUID()).futureValue

        gotRecord shouldBe None
      }

      "get known record" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val createdRecord: Record     = dao.createRecord(record1).futureValue
        val gotRecord: Option[Record] = dao.getRecord(createdRecord.id.get).futureValue

        gotRecord shouldBe Some(createdRecord)
      }
    }

    "updateRecord" - {
      "change book" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val createdRecord: Record = dao.createRecord(record1).futureValue
        val updatedRecord: Option[Record] =
          dao.updateRecord(createdRecord.copy(book = book2)).futureValue

        updatedRecord shouldNot be(Some(createdRecord))
        updatedRecord shouldBe Some(createdRecord.copy(book = book2))
      }
    }

    "deleteRecord" - {
      "delete unknown record" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        dao.createRecord(record1).futureValue
        dao.createRecord(record2).futureValue
        dao.createRecord(record3).futureValue
        val deletedRecord: Option[Record] = dao.deleteRecord(UUID.randomUUID()).futureValue

        deletedRecord shouldBe None
      }

      "delete known record" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        dao.createRecord(record1).futureValue
        val createdRecord: Record = dao.createRecord(record2).futureValue
        dao.createRecord(record3).futureValue
        val deletedRecord: Option[Record] = dao.deleteRecord(createdRecord.id.get).futureValue

        deletedRecord shouldBe Some(createdRecord)
      }
    }

    "findRecord" - {
      "findByUser" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        dao.createRecord(record1).futureValue
        val createdRecord: Record = dao.createRecord(record2).futureValue
        dao.createRecord(record3).futureValue
        val foundRecord: Option[Record] = dao.findByUser(reader).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findByBook" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        dao.createRecord(record1).futureValue
        dao.createRecord(record2).futureValue
        val createdRecord: Record       = dao.createRecord(record3).futureValue
        val foundRecord: Option[Record] = dao.findByBook(book3).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findByGet" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        val createdRecord: Record = dao.createRecord(record1).futureValue
        dao.createRecord(record2).futureValue
        dao.createRecord(record3).futureValue
        val foundRecord: Option[Record] = dao.findByGet(new Timestamp(0)).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findByReturn" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        dao.createRecord(record1).futureValue
        dao.createRecord(record2).futureValue
        val createdRecord: Record = dao.createRecord(record3).futureValue
        val foundRecord: Option[Record] =
          dao.findByReturn(new Timestamp(100100000)).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findAll" in {
        val dao: RecordDao = getDao()
        val record1: Record =
          record.Record(None, admin, book1, new Timestamp(0), new Timestamp(100000000))
        val record2: Record =
          record.Record(None, reader, book2, new Timestamp(10000), new Timestamp(100010000))
        val record3: Record =
          record.Record(None, manager, book3, new Timestamp(100000), new Timestamp(100100000))

        val createdRecords: Seq[Record] =
          Seq(record1, record2, record3).map(dao.createRecord).map(_.futureValue)
        val foundRecords: Seq[Record] = dao.findAll().futureValue

        foundRecords.map(_.id).toSet shouldBe createdRecords.map(_.id).toSet
      }
    }
  }
}

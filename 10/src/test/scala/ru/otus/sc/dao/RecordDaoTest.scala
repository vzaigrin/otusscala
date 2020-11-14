package ru.otus.sc.dao

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{be, convertToAnyShouldWrapper}
import ru.otus.sc.model.Record

abstract class RecordDaoTest(name: String) extends AnyFreeSpec with ScalaFutures {
  def getDao: Dao[Record]
  def destroyDao(): Unit

  val reader: UUID                         = UUID.randomUUID()
  val manager: UUID                        = UUID.randomUUID()
  val admin: UUID                          = UUID.randomUUID()
  val book1: UUID                          = UUID.randomUUID()
  val book2: UUID                          = UUID.randomUUID()
  val book3: UUID                          = UUID.randomUUID()
  val now: LocalDateTime                   = LocalDateTime.now()
  val format: DateTimeFormatter            = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
  def str2time(str: String): LocalDateTime = LocalDateTime.parse(str, format)

  name - {
    "createRecord" - {
      "create one record" in {
        val dao: Dao[Record]      = getDao
        val record1: Record       = Record(None, admin, book1, now, now)
        val createdRecord: Record = dao.create(record1).futureValue

        createdRecord.id shouldNot be(None)
        createdRecord shouldBe record1.copy(id = createdRecord.id)
      }
    }

    "getRecord" - {
      "get unknown record" in {
        val dao: Dao[Record]          = getDao
        val gotRecord: Option[Record] = dao.get(UUID.randomUUID()).futureValue

        gotRecord shouldBe None
      }

      "get known record" in {
        val dao: Dao[Record]          = getDao
        val record1: Record           = Record(None, admin, book1, now, now)
        val createdRecord: Record     = dao.create(record1).futureValue
        val gotRecord: Option[Record] = dao.get(createdRecord.id.get).futureValue

        gotRecord shouldBe Some(createdRecord)
      }
    }

    "updateRecord" - {
      "change book" in {
        val dao: Dao[Record]      = getDao
        val record1: Record       = Record(None, admin, book1, now, now)
        val createdRecord: Record = dao.create(record1).futureValue
        val updatedRecord: Option[Record] =
          dao.update(createdRecord.copy(bookId = book2)).futureValue

        updatedRecord shouldNot be(Some(createdRecord))
        updatedRecord shouldBe Some(createdRecord.copy(bookId = book2))
      }
    }

    "deleteRecord" - {
      "delete unknown record" in {
        val dao: Dao[Record] = getDao
        val record1: Record  = Record(None, admin, book1, now, now)
        val record2: Record  = Record(None, reader, book2, now, now)
        val record3: Record  = Record(None, manager, book3, now, now)

        dao.create(record1).futureValue
        dao.create(record2).futureValue
        dao.create(record3).futureValue
        val deletedRecord: Option[Record] = dao.delete(UUID.randomUUID()).futureValue

        deletedRecord shouldBe None
      }

      "delete known record" in {
        val dao: Dao[Record] = getDao
        val record1: Record  = Record(None, admin, book1, now, now)
        val record2: Record  = Record(None, reader, book2, now, now)
        val record3: Record  = Record(None, manager, book3, now, now)

        dao.create(record1).futureValue
        val createdRecord: Record = dao.create(record2).futureValue
        dao.create(record3).futureValue
        val deletedRecord: Option[Record] = dao.delete(createdRecord.id.get).futureValue

        deletedRecord shouldBe Some(createdRecord)
      }
    }

    "findRecord" - {
      "findByUser" in {
        val dao: Dao[Record] = getDao
        val record1: Record  = Record(None, admin, book1, now, now)
        val record2: Record  = Record(None, reader, book2, now, now)
        val record3: Record  = Record(None, manager, book3, now, now)

        dao.create(record1).futureValue
        val createdRecord: Record = dao.create(record2).futureValue
        dao.create(record3).futureValue
        val foundRecord: Option[Record] =
          dao.findByField("user", reader.toString).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findByBook" in {
        val dao: Dao[Record] = getDao
        val record1: Record  = Record(None, admin, book1, now, now)
        val record2: Record  = Record(None, reader, book2, now, now)
        val record3: Record  = Record(None, manager, book3, now, now)

        dao.create(record1).futureValue
        dao.create(record2).futureValue
        val createdRecord: Record = dao.create(record3).futureValue
        val foundRecord: Option[Record] =
          dao.findByField("book", book3.toString).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findByGet" in {
        val dao: Dao[Record]    = getDao
        val get1: String        = "20201020T152030"
        val ret1: LocalDateTime = str2time("20201025T152030")
        val record1: Record     = Record(None, admin, book1, str2time(get1), ret1)
        val record2: Record     = Record(None, reader, book2, now, now)
        val record3: Record     = Record(None, manager, book3, now, now)

        val createdRecord: Record = dao.create(record1).futureValue
        dao.create(record2).futureValue
        dao.create(record3).futureValue
        val foundRecord: Option[Record] = dao.findByField("get", get1).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findByReturn" in {
        val dao: Dao[Record]    = getDao
        val get1: LocalDateTime = str2time("20201020T152030")
        val ret1: String        = "20201025T152030"
        val record1: Record     = Record(None, admin, book1, now, now)
        val record2: Record     = Record(None, reader, book2, now, now)
        val record3: Record     = Record(None, manager, book3, get1, str2time(ret1))

        dao.create(record1).futureValue
        dao.create(record2).futureValue
        val createdRecord: Record = dao.create(record3).futureValue
        val foundRecord: Option[Record] =
          dao.findByField("return", ret1).futureValue.headOption

        foundRecord shouldBe Some(createdRecord)
      }

      "findAll" in {
        val dao: Dao[Record] = getDao
        val record1: Record  = Record(None, admin, book1, now, now)
        val record2: Record  = Record(None, reader, book2, now, now)
        val record3: Record  = Record(None, manager, book3, now, now)

        val createdRecords: Seq[Record] =
          Seq(record1, record2, record3).map(dao.create).map(_.futureValue)
        val foundRecords: Seq[Record] = dao.findAll().futureValue

        foundRecords.map(_.id).toSet shouldBe createdRecords.map(_.id).toSet
      }
    }
  }
}

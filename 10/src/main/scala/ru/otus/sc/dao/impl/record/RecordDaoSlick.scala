package ru.otus.sc.dao.impl.record

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import ru.otus.sc.dao.Dao
import ru.otus.sc.dao.impl.Slick.records
import ru.otus.sc.model.Record
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class RecordDaoSlick(db: Database)(implicit ec: ExecutionContext) extends Dao[Record] {
  override def init(): Future[Unit] =
    db.run(
      DBIO.seq(
        records.schema.createIfNotExists,
        records.delete
      )
    )

  override def clean(): Future[Unit] = db.run(DBIO.seq(records.delete))

  override def destroy(): Future[Unit] = db.run(DBIO.seq(records.schema.dropIfExists))

  override def create(entity: Record): Future[Record] = {
    val record = entity.asInstanceOf[Record]
    val newId  = UUID.randomUUID()
    val res = for {
      _ <- (records returning records) += RecordRow.fromRecord(record).copy(id = Some(newId))
    } yield record.copy(id = Some(newId))

    db.run(res)
  }

  override def get(id: UUID): Future[Option[Record]] =
    findByCondition(_.id === id).map(_.headOption)

  override def update(entity: Record): Future[Option[Record]] = {
    val record = entity.asInstanceOf[Record]
    record.id match {
      case Some(recordId) =>
        val updateRecord =
          records
            .filter(_.id === recordId)
            .map(r => (r.userId, r.bookId, r.getDT, r.returnDT))
            .update((record.userId, record.bookId, record.getDT, record.returnDT))

        val action =
          for {
            r <- records.filter(_.id === recordId).forUpdate.result.headOption
            _ <- r match {
              case None    => DBIO.successful(())
              case Some(_) => updateRecord
            }
          } yield r.map(_ => record)

        db.run(action.transactionally)

      case None => Future.successful(None)
    }
  }

  override def delete(id: UUID): Future[Option[Record]] = {
    val action =
      for {
        a <- records.filter(_.id === id).forUpdate.result.headOption
        res <- a match {
          case None => DBIO.successful(None)
          case Some(recordRow) =>
            for {
              _ <- records.filter(_.id === id).delete
            } yield Some(recordRow.toRecord)
        }
      } yield res

    db.run(action.transactionally)
  }

  private val format: DateTimeFormatter            = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
  private def str2time(str: String): LocalDateTime = LocalDateTime.parse(str, format)

  private def findByCondition(condition: Records => Rep[Boolean]): Future[Seq[Record]] =
    db.run(records.filter(condition).result).map(_.map { recordRow => recordRow.toRecord })

  override def findByField(field: String, value: String): Future[Seq[Record]] =
    field.toLowerCase match {
      case "user"   => findByCondition(_.userId === UUID.fromString(value))
      case "book"   => findByCondition(_.bookId === UUID.fromString(value))
      case "get"    => findByCondition(_.getDT === str2time(value))
      case "return" => findByCondition(_.returnDT === str2time(value))
      case _        => Future.successful(Seq())
    }

  override def findAll(): Future[Seq[Record]] = findByCondition(_ => true)
}

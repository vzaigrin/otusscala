package ru.otus.sc.dao

import java.sql.Timestamp
import java.util.UUID

import ru.otus.sc.model.book.Book
import ru.otus.sc.model.record.Record
import ru.otus.sc.model.user.User

import scala.concurrent.Future

trait RecordDao {
  def init(): Future[Unit]
  def clean(): Future[Int]
  def destroy(): Future[Unit]
  def createRecord(record: Record): Future[Record]
  def getRecord(id: UUID): Future[Option[Record]]
  def updateRecord(record: Record): Future[Option[Record]]
  def deleteRecord(id: UUID): Future[Option[Record]]
  def findByUser(user: User): Future[Seq[Record]]
  def findByBook(book: Book): Future[Seq[Record]]
  def findByGet(dt: Timestamp): Future[Seq[Record]]
  def findByReturn(dt: Timestamp): Future[Seq[Record]]
  def findAll(): Future[Seq[Record]]
}

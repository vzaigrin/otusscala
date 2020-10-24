package ru.otus.sc.dao.impl.record

import java.util.UUID
import java.sql.Timestamp

import ru.otus.sc.model.book.Book
import ru.otus.sc.model.record
import ru.otus.sc.model.record.Record
import ru.otus.sc.model.user.User

case class RecordRow(
    id: Option[UUID],
    userId: UUID,
    bookId: UUID,
    getDT: Timestamp,
    returnDT: Timestamp
) {
  def toRecord(user: User, book: Book): Record = record.Record(id, user, book, getDT, returnDT)
}

object RecordRow extends ((Option[UUID], UUID, UUID, Timestamp, Timestamp) => RecordRow) {
  def fromRecord(record: Record): RecordRow =
    RecordRow(record.id, record.user.id.get, record.book.id.get, record.getDT, record.returnDT)
}

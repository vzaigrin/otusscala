package ru.otus.sc.dao.impl.record

import java.util.UUID
import java.time.LocalDateTime
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.record
import ru.otus.sc.model.record.Record
import ru.otus.sc.model.user.User

case class RecordRow(
    id: Option[UUID],
    userId: UUID,
    bookId: UUID,
    getDT: LocalDateTime,
    returnDT: LocalDateTime
) {
  def toRecord(user: User, book: Book): Record = record.Record(id, user, book, getDT, returnDT)
}

object RecordRow extends ((Option[UUID], UUID, UUID, LocalDateTime, LocalDateTime) => RecordRow) {
  def fromRecord(record: Record): RecordRow =
    RecordRow(record.id, record.user.id.get, record.book.id.get, record.getDT, record.returnDT)
}

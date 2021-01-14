package ru.otus.sc.dao.impl.record

import java.util.UUID
import java.time.LocalDateTime
import ru.otus.sc.model.Record

case class RecordRow(
    id: Option[UUID],
    userId: UUID,
    bookId: UUID,
    getDT: LocalDateTime,
    returnDT: LocalDateTime
) {
  def toRecord: Record = Record(id, userId, bookId, getDT, returnDT)
}

object RecordRow extends ((Option[UUID], UUID, UUID, LocalDateTime, LocalDateTime) => RecordRow) {
  def fromRecord(record: Record): RecordRow =
    RecordRow(record.id, record.userId, record.bookId, record.getDT, record.returnDT)
}

package ru.otus.sc.model

import java.time.LocalDateTime
import java.util.UUID

// Класс для Записей
case class Record(
    id: Option[UUID],
    userId: UUID,
    bookId: UUID,
    getDT: LocalDateTime,
    returnDT: LocalDateTime
) extends Entity {
  override def toString: String =
    s"id: ${id.getOrElse("")}\tuser: $userId\tbook: $bookId\tget: $getDT\treturn: $returnDT"
}

object Record {
  implicit def ordering[T <: Record]: Ordering[Record] =
    (a: Record, b: Record) => { a.getDT.compareTo(b.getDT) }
}

package ru.otus.sc.model.record

import java.time.LocalDateTime
import java.util.UUID
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.user.User

case class Record(
    id: Option[UUID],
    user: User,
    book: Book,
    getDT: LocalDateTime,
    returnDT: LocalDateTime
) {
  override def toString: String = s"${id.getOrElse("")}\t$user\t$book\t$getDT\t$returnDT"
}

object Record {
  implicit def ordering[T <: Record]: Ordering[Record] =
    (a: Record, b: Record) => { a.getDT.compareTo(b.getDT) }
}

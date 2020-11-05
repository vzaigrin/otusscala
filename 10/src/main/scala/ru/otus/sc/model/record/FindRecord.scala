package ru.otus.sc.model.record

import java.time.LocalDateTime
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.user.User

sealed trait FindRecordRequest

object FindRecordRequest {
  case class ByUser(user: User)          extends FindRecordRequest
  case class ByBook(book: Book)          extends FindRecordRequest
  case class ByGet(dt: LocalDateTime)    extends FindRecordRequest
  case class ByReturn(dt: LocalDateTime) extends FindRecordRequest
  case class All()                       extends FindRecordRequest
}

sealed trait FindRecordResponse

object FindRecordResponse {
  case class Result(records: Seq[Record]) extends FindRecordResponse
}

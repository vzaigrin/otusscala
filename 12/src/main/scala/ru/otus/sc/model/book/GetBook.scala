package ru.otus.sc.model.book

import java.util.UUID

case class GetBookRequest(id: UUID)

sealed trait GetBookResponse

object GetBookResponse {
  case class Found(book: Book)  extends GetBookResponse
  case class NotFound(id: UUID) extends GetBookResponse
}

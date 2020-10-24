package ru.otus.sc.book.model

import java.util.UUID

case class DeleteBookRequest(id: UUID)

sealed trait DeleteBookResponse

object DeleteBookResponse {
  case class Deleted(book: Book) extends DeleteBookResponse
  case class NotFound(id: UUID)  extends DeleteBookResponse
}

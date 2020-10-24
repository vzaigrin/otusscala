package ru.otus.sc.model.book

import java.util.UUID

case class DeleteBookRequest(id: UUID)

sealed trait DeleteBookResponse

object DeleteBookResponse {
  case class Deleted(book: Book) extends DeleteBookResponse
  case class NotFound(id: UUID)  extends DeleteBookResponse
}

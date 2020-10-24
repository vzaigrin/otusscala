package ru.otus.sc.book.model

import java.util.UUID

sealed trait FindBooksRequest

object FindBooksRequest {
  case class ByTitle(title: String) extends FindBooksRequest
  case class ByAuthor(author: UUID) extends FindBooksRequest
  case class ByPages(pages: Int)    extends FindBooksRequest
  case class ByYear(year: Int)      extends FindBooksRequest
  case class All()                  extends FindBooksRequest
}

sealed trait FindBooksResponse

object FindBooksResponse {
  case class Result(books: Seq[Book]) extends FindBooksResponse
}

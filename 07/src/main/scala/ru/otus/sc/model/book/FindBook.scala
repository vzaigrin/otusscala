package ru.otus.sc.model.book

import ru.otus.sc.model.author.Author

sealed trait FindBookRequest

object FindBookRequest {
  case class ByTitle(title: String)   extends FindBookRequest
  case class ByAuthor(author: Author) extends FindBookRequest
  case class ByYear(year: Int)        extends FindBookRequest
  case class ByPages(pages: Int)      extends FindBookRequest
  case class All()                    extends FindBookRequest
}

sealed trait FindBookResponse

object FindBookResponse {
  case class Result(books: Seq[Book]) extends FindBookResponse
}

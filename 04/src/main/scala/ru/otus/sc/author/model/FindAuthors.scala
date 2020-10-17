package ru.otus.sc.author.model

sealed trait FindAuthorsRequest

object FindAuthorsRequest {
  case class ByLastName(lastName: String) extends FindAuthorsRequest
  case class All()                        extends FindAuthorsRequest
}

sealed trait FindAuthorsResponse

object FindAuthorsResponse {
  case class Result(authors: Seq[Author]) extends FindAuthorsResponse
}

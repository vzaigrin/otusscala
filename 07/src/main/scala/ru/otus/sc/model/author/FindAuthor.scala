package ru.otus.sc.model.author

sealed trait FindAuthorRequest

object FindAuthorRequest {
  case class ByLastName(lastName: String) extends FindAuthorRequest
  case class All()                        extends FindAuthorRequest
}

sealed trait FindAuthorResponse

object FindAuthorResponse {
  case class Result(authors: Seq[Author]) extends FindAuthorResponse
}

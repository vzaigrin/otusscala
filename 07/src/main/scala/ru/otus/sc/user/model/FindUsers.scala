package ru.otus.sc.user.model

sealed trait FindUsersRequest
object FindUsersRequest {
  case class ByLastName(lastName: String) extends FindUsersRequest
  case class ByRole(role: Role)           extends FindUsersRequest
  case class All()                        extends FindUsersRequest
}

sealed trait FindUsersResponse
object FindUsersResponse {
  case class Result(users: Seq[User]) extends FindUsersResponse
}
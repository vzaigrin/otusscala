package ru.otus.sc.model.user

import ru.otus.sc.model.role.Role

sealed trait FindUserRequest

object FindUserRequest {
  case class ByUserName(uerName: String)  extends FindUserRequest
  case class ByLastName(lastName: String) extends FindUserRequest
  case class ByRole(role: Role)           extends FindUserRequest
  case class All()                        extends FindUserRequest
}

sealed trait FindUserResponse

object FindUserResponse {
  case class Result(users: Seq[User]) extends FindUserResponse
}

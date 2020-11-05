package ru.otus.sc.model.user

import java.util.UUID

case class UpdateUserRequest(user: User)

sealed trait UpdateUserResponse

object UpdateUserResponse {
  case class Updated(user: User)      extends UpdateUserResponse
  case class NotFound(id: UUID)       extends UpdateUserResponse
  case object CantUpdateUserWithoutId extends UpdateUserResponse
}

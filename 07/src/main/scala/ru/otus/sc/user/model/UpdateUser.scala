package ru.otus.sc.user.model

import java.util.UUID

case class UpdateUserRequest(user: User)

sealed trait UpdateUserResponse
object UpdateUserResponse {
  case class Updated(user: User)      extends UpdateUserResponse
  case class NotFound(userId: UUID)   extends UpdateUserResponse
  case object CantUpdateUserWithoutId extends UpdateUserResponse
}

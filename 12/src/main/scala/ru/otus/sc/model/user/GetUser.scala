package ru.otus.sc.model.user

import java.util.UUID

case class GetUserRequest(id: UUID)

sealed trait GetUserResponse

object GetUserResponse {
  case class Found(user: User)  extends GetUserResponse
  case class NotFound(id: UUID) extends GetUserResponse
}

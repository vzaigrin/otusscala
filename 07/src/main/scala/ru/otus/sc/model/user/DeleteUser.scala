package ru.otus.sc.model.user

import java.util.UUID

case class DeleteUserRequest(id: UUID)

sealed trait DeleteUserResponse

object DeleteUserResponse {
  case class Deleted(user: User) extends DeleteUserResponse
  case class NotFound(id: UUID)  extends DeleteUserResponse
}

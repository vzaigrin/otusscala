package ru.otus.sc.model.role

import java.util.UUID

case class DeleteRoleRequest(id: UUID)

sealed trait DeleteRoleResponse

object DeleteRoleResponse {
  case class Deleted(role: Role) extends DeleteRoleResponse
  case class NotFound(id: UUID)  extends DeleteRoleResponse
}

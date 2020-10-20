package ru.otus.sc.model.role

import java.util.UUID

case class GetRoleRequest(id: UUID)

sealed trait GetRoleResponse

object GetRoleResponse {
  case class Found(role: Role)  extends GetRoleResponse
  case class NotFound(id: UUID) extends GetRoleResponse
}

package ru.otus.sc.model.role

import java.util.UUID

case class UpdateRoleRequest(role: Role)

sealed trait UpdateRoleResponse

object UpdateRoleResponse {
  case class Updated(role: Role)      extends UpdateRoleResponse
  case class NotFound(id: UUID)       extends UpdateRoleResponse
  case object CantUpdateRoleWithoutId extends UpdateRoleResponse
}

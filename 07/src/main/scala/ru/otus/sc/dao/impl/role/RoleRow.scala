package ru.otus.sc.dao.impl.role

import java.util.UUID

import ru.otus.sc.model.role.Role

case class RoleRow(
    id: Option[UUID],
    roleName: String
) {
  def toRole: Role = Role(id, roleName)
}

object RoleRow extends ((Option[UUID], String) => RoleRow) {
  def fromRole(role: Role): RoleRow = RoleRow(role.id, role.name)
}

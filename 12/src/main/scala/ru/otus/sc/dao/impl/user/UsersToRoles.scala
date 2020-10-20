package ru.otus.sc.dao.impl.user

import java.util.UUID
import ru.otus.sc.dao.impl.Slick.{roles, users}
import ru.otus.sc.dao.impl.role.{RoleRow, Roles}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ForeignKeyQuery

class UsersToRoles(tag: Tag) extends Table[(UUID, UUID)](tag, "users_to_roles") {
  val userId = column[UUID]("userid")
  val roleId = column[UUID]("roleid")

  val * = (userId, roleId)

  def user: ForeignKeyQuery[Users, UserRow] =
    foreignKey("userid", userId, users)(
      _.id,
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )

  def role: ForeignKeyQuery[Roles, RoleRow] =
    foreignKey("roleid", roleId, roles)(
      _.id,
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )
}

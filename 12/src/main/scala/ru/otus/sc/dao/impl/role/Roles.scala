package ru.otus.sc.dao.impl.role

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

class Roles(tag: Tag) extends Table[RoleRow](tag, "roles") {
  val id       = column[UUID]("id", O.PrimaryKey, O.Unique)
  val roleName = column[String]("rolename")

  val * = (id.?, roleName).mapTo[RoleRow]
}

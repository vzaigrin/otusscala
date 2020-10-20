package ru.otus.sc.dao.impl.user

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

class Users(tag: Tag) extends Table[UserRow](tag, "users") {
  val id        = column[UUID]("id", O.PrimaryKey, O.Unique)
  val userName  = column[String]("username", O.Unique)
  val password  = column[String]("password")
  val firstName = column[String]("firstname")
  val lastName  = column[String]("lastname")
  val age       = column[Int]("age")

  val * = (id.?, userName, password, firstName, lastName, age).mapTo[UserRow]
}

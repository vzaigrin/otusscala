package ru.otus.sc.dao.impl.author

import java.util.UUID

import slick.jdbc.PostgresProfile.api._

class Authors(tag: Tag) extends Table[AuthorRow](tag, "authors") {
  val id        = column[UUID]("id", O.PrimaryKey, O.Unique)
  val firstName = column[String]("firstname")
  val lastName  = column[String]("lastname")

  val * = (id.?, firstName, lastName).mapTo[AuthorRow]
}

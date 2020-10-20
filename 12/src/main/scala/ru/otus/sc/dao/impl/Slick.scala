package ru.otus.sc.dao.impl

import ru.otus.sc.dao.impl.author.Authors
import ru.otus.sc.dao.impl.book.{Books, BooksToAuthors}
import ru.otus.sc.dao.impl.record.Records
import ru.otus.sc.dao.impl.role.Roles
import ru.otus.sc.dao.impl.user.{Users, UsersToRoles}
import slick.jdbc.PostgresProfile.api._

object Slick {
  val roles            = TableQuery[Roles]
  val users            = TableQuery[Users]
  val users_to_roles   = TableQuery[UsersToRoles]
  val authors          = TableQuery[Authors]
  val books            = TableQuery[Books]
  val books_to_authors = TableQuery[BooksToAuthors]
  val records          = TableQuery[Records]
}

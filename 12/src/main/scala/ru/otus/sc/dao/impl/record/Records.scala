package ru.otus.sc.dao.impl.record

import java.sql.Timestamp
import java.util.UUID
import ru.otus.sc.dao.impl.Slick.{books, users}
import ru.otus.sc.dao.impl.book.{BookRow, Books}
import ru.otus.sc.dao.impl.user.{UserRow, Users}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ForeignKeyQuery

class Records(tag: Tag) extends Table[RecordRow](tag, "book_records") {
  val id       = column[UUID]("id", O.PrimaryKey, O.Unique)
  val userId   = column[UUID]("userid")
  val bookId   = column[UUID]("bookid")
  val getDT    = column[Timestamp]("getdt")
  val returnDT = column[Timestamp]("returndt")

  val * = (id.?, userId, bookId, getDT, returnDT).mapTo[RecordRow]

  def user: ForeignKeyQuery[Users, UserRow] =
    foreignKey("userid", userId, users)(
      _.id,
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )

  def book: ForeignKeyQuery[Books, BookRow] =
    foreignKey("bookid", bookId, books)(
      _.id,
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )
}

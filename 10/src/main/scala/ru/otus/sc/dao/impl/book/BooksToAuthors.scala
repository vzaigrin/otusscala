package ru.otus.sc.dao.impl.book

import java.util.UUID
import ru.otus.sc.dao.impl.Slick.{authors, books}
import ru.otus.sc.dao.impl.author.{AuthorRow, Authors}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ForeignKeyQuery

class BooksToAuthors(tag: Tag) extends Table[(UUID, UUID)](tag, "books_to_authors") {
  val bookId   = column[UUID]("bookid")
  val authorId = column[UUID]("authorid")

  val * = (bookId, authorId)

  def book: ForeignKeyQuery[Books, BookRow] =
    foreignKey("bookid", bookId, books)(
      _.id,
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )

  def author: ForeignKeyQuery[Authors, AuthorRow] =
    foreignKey("authorid", authorId, authors)(
      _.id,
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )
}

package ru.otus.sc.dao.impl.book

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

class Books(tag: Tag) extends Table[BookRow](tag, "books") {
  val id        = column[UUID]("id", O.PrimaryKey, O.Unique)
  val title     = column[String]("title")
  val published = column[Int]("published")
  val pages     = column[Int]("pages")

  val * = (id.?, title, published, pages).mapTo[BookRow]
}

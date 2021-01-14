package ru.otus.sc.model

import java.util.UUID

// Класс для Книг
case class Book(
    id: Option[UUID],
    title: String,
    authors: Set[UUID],
    published: Int,
    pages: Int
) extends Entity {
  override def toString: String = {
    val authorsString: String = authors.toSeq.mkString(", ")
    s"$title,\tauthors: $authorsString,\t year: $published,\tpages: $pages,\tid: ${id.getOrElse("")}"
  }
}

object Book {
  implicit def ordering[T <: Book]: Ordering[Book] =
    (a: Book, b: Book) => { a.title.compareTo(b.title) }
}

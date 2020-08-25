package ru.otus.sc.book.model

import java.util.UUID

case class Book(
    id: Option[UUID],
    title: String,
    authors: Set[UUID],
    published: Int,
    pages: Int
) {
  override def toString: String = {
    val authorsString: String = authors.map(a => s"$a").mkString(", ")
    s"$title,\tauthors: $authorsString,\t year: $published,\tpages: $pages,\tid: ${id.getOrElse("")}"
  }
}

object Book {
  implicit def ordering[T <: Book]: Ordering[T] =
    (a: T, b: T) => {
      a.title.compareTo(b.title)
    }
}

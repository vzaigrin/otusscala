package ru.otus.sc.model.book

import java.util.UUID

import ru.otus.sc.model.author.Author

case class Book(
    id: Option[UUID],
    title: String,
    authors: Set[Author],
    published: Int,
    pages: Int
) {
  override def toString: String = {
    val authorsString: String =
      authors.toSeq.sorted.map(a => s"${a.lastName} ${a.firstName}").mkString(", ")
    s"$title,\tauthors: $authorsString,\t year: $published,\tpages: $pages,\tid: ${id.getOrElse("")}"
  }
}

object Book {
  implicit def ordering[T <: Book]: Ordering[Book] =
    (a: Book, b: Book) => { a.title.compareTo(b.title) }
}

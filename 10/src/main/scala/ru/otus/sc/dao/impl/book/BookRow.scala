package ru.otus.sc.dao.impl.book

import java.util.UUID

import ru.otus.sc.model.{Author, Book}

case class BookRow(
    id: Option[UUID],
    title: String,
    published: Int,
    pages: Int
) {
  def toBook(authors: Set[UUID]): Book = Book(id, title, authors, published, pages)
}

object BookRow extends ((Option[UUID], String, Int, Int) => BookRow) {
  def fromBook(book: Book): BookRow = BookRow(book.id, book.title, book.published, book.pages)
}

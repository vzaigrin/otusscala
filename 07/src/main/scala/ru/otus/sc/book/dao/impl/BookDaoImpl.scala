package ru.otus.sc.book.dao.impl

import java.util.UUID
import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model.Book

class BookDaoImpl extends BookDao {
  private var books: Map[UUID, Book] = Map.empty

  override def createBook(book: Book): Book = {
    val id         = UUID.randomUUID()
    val bookWithId = book.copy(id = Some(id))
    books += (id -> bookWithId)
    bookWithId
  }

  override def getBook(id: UUID): Option[Book] = books.get(id)

  override def updateBook(book: Book): Option[Book] =
    for {
      id <- book.id
      _  <- books.get(id)
    } yield {
      books += (id -> book)
      book
    }

  override def deleteBook(id: UUID): Option[Book] =
    books.get(id) match {
      case Some(book) =>
        books -= id
        Some(book)
      case None => None
    }

  override def findByTitle(title: String): Seq[Book] =
    books.values.filter(_.title == title).toVector

  override def findByAuthor(author: UUID): Seq[Book] =
    books.values.filter(_.authors.contains(author)).toVector

  override def findByPages(pages: Int): Seq[Book] =
    books.values.filter(_.pages == pages).toVector

  override def findByYear(year: Int): Seq[Book] =
    books.values.filter(_.published == year).toVector

  override def findAll(): Seq[Book] = books.values.toVector
}

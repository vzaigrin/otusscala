package ru.otus.sc.book.dao

import java.util.UUID
import ru.otus.sc.book.model.Book

trait BookDao {
  def createBook(book: Book): Book
  def getBook(id: UUID): Option[Book]
  def updateBook(book: Book): Option[Book]
  def deleteBook(id: UUID): Option[Book]
  def findByTitle(title: String): Seq[Book]
  def findByAuthor(author: UUID): Seq[Book]
  def findByYear(year: Int): Seq[Book]
  def findByPages(pages: Int): Seq[Book]
  def findAll(): Seq[Book]
}

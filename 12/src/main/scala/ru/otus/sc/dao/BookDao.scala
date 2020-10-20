package ru.otus.sc.dao

import java.util.UUID

import ru.otus.sc.model.author.Author
import ru.otus.sc.model.book.Book

import scala.concurrent.Future

trait BookDao {
  def init(): Future[Unit]
  def clean(): Future[Unit]
  def destroy(): Future[Unit]
  def createBook(book: Book): Future[Book]
  def getBook(id: UUID): Future[Option[Book]]
  def updateBook(book: Book): Future[Option[Book]]
  def deleteBook(id: UUID): Future[Option[Book]]
  def findByTitle(title: String): Future[Seq[Book]]
  def findByAuthor(author: Author): Future[Seq[Book]]
  def findByYear(year: Int): Future[Seq[Book]]
  def findByPages(pages: Int): Future[Seq[Book]]
  def findAll(): Future[Seq[Book]]
}

package ru.otus.sc.dao

import java.util.UUID

import ru.otus.sc.model.author.Author

import scala.concurrent.Future

trait AuthorDao {
  def init(): Future[Unit]
  def clean(): Future[Unit]
  def destroy(): Future[Unit]
  def createAuthor(author: Author): Future[Author]
  def getAuthor(id: UUID): Future[Option[Author]]
  def updateAuthor(author: Author): Future[Option[Author]]
  def deleteAuthor(id: UUID): Future[Option[Author]]
  def findByLastName(lastName: String): Future[Seq[Author]]
  def findAll(): Future[Seq[Author]]
}

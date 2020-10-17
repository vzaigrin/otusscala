package ru.otus.sc.author.dao

import java.util.UUID
import ru.otus.sc.author.model.Author

trait AuthorDao {
  def createAuthor(author: Author): Author
  def getAuthor(id: UUID): Option[Author]
  def updateAuthor(author: Author): Option[Author]
  def deleteAuthor(id: UUID): Option[Author]
  def findByLastName(lastName: String): Seq[Author]
  def findAll(): Seq[Author]
}

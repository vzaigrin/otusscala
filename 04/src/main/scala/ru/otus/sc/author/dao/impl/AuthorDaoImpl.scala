package ru.otus.sc.author.dao.impl

import java.util.UUID
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model.Author

class AuthorDaoImpl extends AuthorDao {
  private var authors: Map[UUID, Author] = Map.empty

  override def createAuthor(author: Author): Author = {
    val id           = UUID.randomUUID()
    val authorWithId = author.copy(id = Some(id))
    authors += (id -> authorWithId)
    authorWithId
  }

  override def getAuthor(id: UUID): Option[Author] = authors.get(id)

  override def updateAuthor(author: Author): Option[Author] =
    for {
      id <- author.id
      _  <- authors.get(id)
    } yield {
      authors += (id -> author)
      author
    }

  override def deleteAuthor(id: UUID): Option[Author] =
    authors.get(id) match {
      case Some(author) =>
        authors -= id
        Some(author)
      case None => None
    }

  override def findByLastName(lastName: String): Seq[Author] =
    authors.values.filter(_.lastName == lastName).toVector

  override def findAll(): Seq[Author] = authors.values.toVector
}

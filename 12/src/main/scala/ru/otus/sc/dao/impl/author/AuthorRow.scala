package ru.otus.sc.dao.impl.author

import java.util.UUID

import ru.otus.sc.model.author.Author

case class AuthorRow(
    id: Option[UUID],
    firstName: String,
    lastName: String
) {
  def toAuthor: Author = Author(id, firstName, lastName)
}

object AuthorRow extends ((Option[UUID], String, String) => AuthorRow) {
  def fromAuthor(author: Author): AuthorRow =
    AuthorRow(author.id, author.firstName, author.lastName)
}

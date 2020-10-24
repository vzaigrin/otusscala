package ru.otus.sc.author.model

import java.util.UUID

case class Author(
    id: Option[UUID],
    firstName: String,
    lastName: String
) {
  override def toString: String = s"$lastName $firstName,\tid: ${id.getOrElse("")}"
}

object Author {
  implicit val ordering: Ordering[Author] =
    (a, b) => { a.lastName.compareTo(b.lastName) }
}

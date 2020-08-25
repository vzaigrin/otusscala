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
  def apply() = new Author(None, "UNKNOWN", "UNKNOWN")
  implicit def ordering[T <: Author]: Ordering[T] =
    (a: T, b: T) => {
      a.lastName.compareTo(b.lastName)
    }
}

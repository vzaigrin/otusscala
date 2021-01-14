package ru.otus.sc.model

import java.util.UUID

// Класс для Авторов
case class Author(
    id: Option[UUID],
    firstName: String,
    lastName: String
) extends Entity {
  override def toString: String = s"$lastName $firstName,\tid: ${id.getOrElse("")}"
}

object Author {
  implicit def ordering[T <: Author]: Ordering[Author] =
    (a: Author, b: Author) => { a.lastName.compareTo(b.lastName) }
}

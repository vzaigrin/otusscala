package ru.otus.sc.model

import java.util.UUID

// Класс для Ролей
case class Role(
    id: Option[UUID],
    name: String
) extends Entity {
  override def toString: String = s"$name,\tid: $id"
}

object Role {
  implicit def ordering[T <: Role]: Ordering[Role] =
    (a: Role, b: Role) => { a.name.compareTo(b.name) }
}

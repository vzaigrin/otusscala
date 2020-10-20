package ru.otus.sc.model.role

import java.util.UUID

case class Role(
    id: Option[UUID],
    name: String
) {
  override def toString: String = s"$name,\tid: $id"
}

object Role {
  implicit def ordering[T <: Role]: Ordering[Role] =
    (a: Role, b: Role) => { a.name.compareTo(b.name) }
}

package ru.otus.sc.user.model

import java.util.UUID

sealed trait Role

object Role {
  case object Reader  extends Role
  case object Manager extends Role
  case object Admin   extends Role
}

case class User(
    id: Option[UUID],
    firstName: String,
    lastName: String,
    age: Int,
    roles: Set[Role]
) {
  override def toString: String = {
    val roleNames: String = roles.map(_.toString).mkString(", ")
    s"$lastName $firstName,\tage = $age, roles: $roleNames,\tid: ${id.getOrElse("")}"
  }
}

object User {
  implicit def ordering[T <: User]: Ordering[T] =
    (a: T, b: T) => {
      a.lastName.compareTo(b.lastName)
    }
}

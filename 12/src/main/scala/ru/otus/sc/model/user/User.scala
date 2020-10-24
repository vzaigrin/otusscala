package ru.otus.sc.model.user

import java.util.UUID

import ru.otus.sc.model.role.Role

case class User(
    id: Option[UUID],
    userName: String,
    password: String,
    firstName: String,
    lastName: String,
    age: Int,
    roles: Set[Role]
) {
  override def toString: String = {
    val roleNames: String = roles.toSeq.sorted.map(_.name).mkString(", ")
    s"$userName, $lastName $firstName,\tage = $age, roles: $roleNames,\tid: ${id.getOrElse("")}"
  }
}

object User {
  implicit def ordering[T <: User]: Ordering[User] =
    (a: User, b: User) => { a.userName.compareTo(b.userName) }
}

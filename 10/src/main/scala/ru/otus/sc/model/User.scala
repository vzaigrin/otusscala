package ru.otus.sc.model

import java.util.UUID

// Класс для Пользователей
case class User(
    id: Option[UUID],
    userName: String,
    password: String,
    firstName: String,
    lastName: String,
    age: Int,
    roles: Set[UUID]
) extends Entity {
  override def toString: String = {
    val roleIds: String = roles.toSeq.mkString(", ")
    s"$userName, $lastName $firstName,\tage = $age, roles: $roleIds,\tid: ${id.getOrElse("")}"
  }
}

object User {
  implicit def ordering[T <: User]: Ordering[User] =
    (a: User, b: User) => { a.userName.compareTo(b.userName) }
}

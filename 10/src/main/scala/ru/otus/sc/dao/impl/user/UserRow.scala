package ru.otus.sc.dao.impl.user

import java.util.UUID
import ru.otus.sc.model.{Role, User}

case class UserRow(
    id: Option[UUID],
    userName: String,
    password: String,
    firstName: String,
    lastName: String,
    age: Int
) {
  def toUser(roles: Set[UUID]): User = User(id, userName, password, firstName, lastName, age, roles)
}

object UserRow extends ((Option[UUID], String, String, String, String, Int) => UserRow) {
  def fromUser(user: User): UserRow =
    UserRow(user.id, user.userName, user.password, user.firstName, user.lastName, user.age)
}

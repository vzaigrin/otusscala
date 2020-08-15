package ru.otus.sc.user.dao.map

import java.util.UUID

import ru.otus.sc.user.dao.UserDao
import ru.otus.sc.user.model.User

class UserDaoMapImpl extends UserDao {
  private var users: Map[UUID, User] = Map.empty

  def createUser(user: User): User = {
    val id         = UUID.randomUUID()
    val userWithId = user.copy(id = Some(id))
    users += (id -> userWithId)
    userWithId
  }

  def getUser(userId: UUID): Option[User] = users.get(userId)

  def updateUser(user: User): Option[User] =
    for {
      id <- user.id
      _  <- users.get(id)
    } yield {
      users += (id -> user)
      user
    }

  def deleteUser(userId: UUID): Option[User] =
    users.get(userId) match {
      case Some(user) =>
        users -= userId
        Some(user)
      case None => None
    }

  def findByLastName(lastName: String): Seq[User] =
    users.values.filter(_.lastName == lastName).toVector

  def findAll(): Seq[User] = users.values.toVector
}

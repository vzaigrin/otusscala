package ru.otus.sc.user.dao

import java.util.UUID

import ru.otus.sc.user.model.User

trait UserDao {
  def createUser(user: User): User
  def getUser(userId: UUID): Option[User]
  def updateUser(user: User): Option[User]
  def deleteUser(userId: UUID): Option[User]
  def findByLastName(lastName: String): Seq[User]
  def findAll(): Seq[User]
}

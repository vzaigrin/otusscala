package ru.otus.sc.dao

import java.util.UUID

import ru.otus.sc.model.role.Role
import ru.otus.sc.model.user.User

import scala.concurrent.Future

trait UserDao {
  def init(): Future[Unit]
  def clean(): Future[Unit]
  def destroy(): Future[Unit]
  def createUser(user: User): Future[User]
  def getUser(id: UUID): Future[Option[User]]
  def updateUser(user: User): Future[Option[User]]
  def deleteUser(id: UUID): Future[Option[User]]
  def findByUserName(uerName: String): Future[Seq[User]]
  def findByLastName(lastName: String): Future[Seq[User]]
  def findByRole(role: Role): Future[Seq[User]]
  def findAll(): Future[Seq[User]]
}

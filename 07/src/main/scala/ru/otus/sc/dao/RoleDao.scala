package ru.otus.sc.dao

import java.util.UUID

import ru.otus.sc.model.role.Role

import scala.concurrent.Future

trait RoleDao {
  def init(): Future[Unit]
  def clean(): Future[Unit]
  def destroy(): Future[Unit]
  def createRole(role: Role): Future[Role]
  def getRole(id: UUID): Future[Option[Role]]
  def updateRole(role: Role): Future[Option[Role]]
  def deleteRole(id: UUID): Future[Option[Role]]
  def findByName(name: String): Future[Seq[Role]]
  def findAll(): Future[Seq[Role]]
}

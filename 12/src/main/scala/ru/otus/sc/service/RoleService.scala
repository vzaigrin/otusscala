package ru.otus.sc.service

import ru.otus.sc.model.role._
import scala.concurrent.Future

trait RoleService {
  def createRole(request: CreateRoleRequest): Future[CreateRoleResponse]
  def getRole(request: GetRoleRequest): Future[GetRoleResponse]
  def updateRole(request: UpdateRoleRequest): Future[UpdateRoleResponse]
  def deleteRole(request: DeleteRoleRequest): Future[DeleteRoleResponse]
  def findRole(request: FindRoleRequest): Future[FindRoleResponse]
}

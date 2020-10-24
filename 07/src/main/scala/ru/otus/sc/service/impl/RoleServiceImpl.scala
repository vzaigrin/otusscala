package ru.otus.sc.service.impl

import ru.otus.sc.dao.RoleDao
import ru.otus.sc.model.role._
import ru.otus.sc.service.RoleService
import scala.concurrent.{ExecutionContext, Future}

class RoleServiceImpl(dao: RoleDao)(implicit ec: ExecutionContext) extends RoleService {
  override def createRole(request: CreateRoleRequest): Future[CreateRoleResponse] =
    dao.createRole(request.role).map(CreateRoleResponse)

  override def getRole(request: GetRoleRequest): Future[GetRoleResponse] =
    dao.getRole(request.id) map {
      case Some(role) => GetRoleResponse.Found(role)
      case None       => GetRoleResponse.NotFound(request.id)
    }

  override def updateRole(request: UpdateRoleRequest): Future[UpdateRoleResponse] =
    request.role.id match {
      case None => Future.successful(UpdateRoleResponse.CantUpdateRoleWithoutId)
      case Some(roleId) =>
        dao.updateRole(request.role) map {
          case Some(role) => UpdateRoleResponse.Updated(role)
          case None       => UpdateRoleResponse.NotFound(roleId)
        }
    }

  override def deleteRole(request: DeleteRoleRequest): Future[DeleteRoleResponse] =
    dao
      .deleteRole(request.id)
      .map {
        _.map(DeleteRoleResponse.Deleted)
          .getOrElse(DeleteRoleResponse.NotFound(request.id))
      }

  override def findRole(request: FindRoleRequest): Future[FindRoleResponse] =
    request match {
      case FindRoleRequest.ByName(name) => dao.findByName(name).map(FindRoleResponse.Result)
      case FindRoleRequest.All()        => dao.findAll().map(FindRoleResponse.Result)
    }

}

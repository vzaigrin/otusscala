package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.model.role._
import ru.otus.sc.service.RoleService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.json.RoleJsonProtocol._

class RoleRouter(service: RoleService) extends BaseRouter {
  def route: Route =
    pathPrefix("role") {
      getRole ~
        findRoleByName ~
        findAllRoles ~
        createRole ~
        updateRole ~
        deleteRole
    }

  private def getRole: Route = {
    (get & parameter("id".as[UUID])) { uuid =>
      onSuccess(service.getRole(GetRoleRequest(uuid))) {
        case GetRoleResponse.Found(role) => complete(role)
        case GetRoleResponse.NotFound(_) => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findRoleByName: Route = {
    (get & parameter("name".as[String])) { name =>
      onSuccess(service.findRole(FindRoleRequest.ByName(name))) {
        case FindRoleResponse.Result(roleSeq) => complete(roleSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findAllRoles: Route =
    get {
      onSuccess(service.findRole(FindRoleRequest.All())) {
        case FindRoleResponse.Result(roleSeq) => complete(roleSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }

  private def createRole: Route =
    (post & entity(as[Role])) { role =>
      onSuccess(service.createRole(CreateRoleRequest(role))) { response =>
        complete(response.role)
      }
    }

  private def updateRole: Route =
    (put & entity(as[Role])) { role =>
      onSuccess(service.updateRole(UpdateRoleRequest(role))) {
        case UpdateRoleResponse.Updated(role)           => complete(role)
        case UpdateRoleResponse.NotFound(_)             => complete(StatusCodes.NotFound)
        case UpdateRoleResponse.CantUpdateRoleWithoutId => complete(StatusCodes.BadRequest)
      }
    }

  private def deleteRole: Route =
    (delete & path(JavaUUID.map(DeleteRoleRequest))) { roleIdRequest =>
      onSuccess(service.deleteRole(roleIdRequest)) {
        case DeleteRoleResponse.Deleted(role) => complete(role)
        case DeleteRoleResponse.NotFound(_)   => complete(StatusCodes.NotFound)
      }
    }
}

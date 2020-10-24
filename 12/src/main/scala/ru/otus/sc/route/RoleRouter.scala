package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.concat
import ru.otus.sc.model.role._
import ru.otus.sc.service.RoleService
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import io.circe.Encoder._
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import scala.concurrent.{ExecutionContextExecutor, Future}

class RoleRouter(pathPrefix: String, service: RoleService) extends BaseRouter {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def route: Route =
    concat(
      findAllRolesRoute,
      getRoleRoute,
      findRoleByNameRoute,
      createRoleRoute,
      deleteRoleRoute(),
      updateRoleRoute()
    )

  def endpoints =
    List(
      findAllRolesEndpoint,
      getRoleEndpoint,
      findRoleByNameEndpoint,
      createRoleEndpoint,
      deleteRoleEndpoint(),
      updateRoleEndpoint()
    )

  private val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.in(pathPrefix).in("role").errorOut(stringBody)

  // Выводим все роли
  private def findAllRolesEndpoint: Endpoint[Unit, String, Seq[Role], Any] =
    baseEndpoint.get
      .description("Вывод всех ролей")
      .out(jsonBody[Seq[Role]])

  private def findAllRoles: Future[Either[String, Seq[Role]]] = {
    service.findRole(FindRoleRequest.All()) map {
      case FindRoleResponse.Result(roleSeq) => Right(roleSeq)
      case _                                => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findAllRolesRoute: Route = findAllRolesEndpoint.toRoute(_ => findAllRoles)

  // Выводим роль по id
  private def getRoleEndpoint: Endpoint[UUID, String, Role, Any] =
    baseEndpoint.get
      .description("Вывод конкретной роли по Id")
      .in(path[UUID])
      .out(jsonBody[Role])

  private def getRole(id: UUID): Future[Either[String, Role]] = {
    service.getRole(GetRoleRequest(id)) map {
      case GetRoleResponse.Found(role) => Right(role)
      case GetRoleResponse.NotFound(_) => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def getRoleRoute: Route = getRoleEndpoint.toRoute(getRole)

  // Ищем и выводим роли по имени
  private def findRoleByNameEndpoint: Endpoint[String, String, Seq[Role], Any] =
    baseEndpoint.get
      .description("Поиск ролей по имени")
      .in("find")
      .in(query[String]("name"))
      .out(jsonBody[Seq[Role]])

  private def findRole(name: String): Future[Either[String, Seq[Role]]] = {
    service.findRole(FindRoleRequest.ByName(name)) map {
      case FindRoleResponse.Result(role) => Right(role)
      case _                             => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findRoleByNameRoute: Route = findRoleByNameEndpoint.toRoute(findRole)

  // Создаём роль
  private def createRoleEndpoint: Endpoint[Role, String, Role, Any] =
    baseEndpoint.post
      .description("Создаём роль")
      .in(jsonBody[Role])
      .out(jsonBody[Role])

  private def createRole(role: Role): Future[Either[String, Role]] =
    service.createRole(CreateRoleRequest(role)) map {
      case CreateRoleResponse(response) => Right(response)
      case _                            => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def createRoleRoute: Route = createRoleEndpoint.toRoute(createRole)

  // Удаляем роль по Id
  private def deleteRoleEndpoint(): Endpoint[UUID, String, Role, Any] =
    baseEndpoint.delete
      .description("Удаляем роль по Id")
      .in(path[UUID])
      .out(jsonBody[Role])

  private def deleteRole(id: UUID): Future[Either[String, Role]] =
    service.deleteRole(DeleteRoleRequest(id)) map {
      case DeleteRoleResponse.Deleted(role) => Right(role)
      case DeleteRoleResponse.NotFound(_)   => Left(StatusCodes.NotFound.defaultMessage)
    }

  private def deleteRoleRoute(): Route = deleteRoleEndpoint().toRoute(deleteRole)

  // Обновляем роль
  private def updateRoleEndpoint(): Endpoint[Role, String, Role, Any] =
    baseEndpoint.put
      .description("Обновляем роль")
      .in(jsonBody[Role])
      .out(jsonBody[Role])

  private def updateRole(role: Role): Future[Either[String, Role]] =
    service.updateRole(UpdateRoleRequest(role)) map {
      case UpdateRoleResponse.Updated(role)           => Right(role)
      case UpdateRoleResponse.NotFound(_)             => Left(StatusCodes.NotFound.defaultMessage)
      case UpdateRoleResponse.CantUpdateRoleWithoutId => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def updateRoleRoute(): Route = updateRoleEndpoint().toRoute(updateRole)
}

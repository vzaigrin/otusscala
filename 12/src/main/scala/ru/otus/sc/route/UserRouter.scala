package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.concat
import ru.otus.sc.model.user._
import ru.otus.sc.service.UserService
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import ru.otus.sc.model.role.Role
import sttp.model._
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import io.circe.Encoder._
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import scala.concurrent.{ExecutionContextExecutor, Future}

class UserRouter(pathPrefix: String, service: UserService) extends BaseRouter {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def route: Route =
    concat(
      findAllUsersRoute,
      getUserRoute,
      findUsersRoute,
      createUserRoute,
      deleteUserRoute(),
      updateUserRoute()
    )

  def endpoints =
    List(
      findAllUsersEndpoint,
      getUserEndpoint,
      findUsersEndpoint,
      createUserEndpoint,
      deleteUserEndpoint(),
      updateUserEndpoint()
    )

  private val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.in(pathPrefix).in("user").errorOut(stringBody)

  // Выводим всех пользователей
  private def findAllUsersEndpoint: Endpoint[Unit, String, Seq[User], Any] =
    baseEndpoint.get
      .description("Вывод всех пользователей")
      .out(jsonBody[Seq[User]])

  private def findAllUsers: Future[Either[String, Seq[User]]] = {
    service.findUser(FindUserRequest.All()) map {
      case FindUserResponse.Result(userSeq) => Right(userSeq)
      case _                                => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findAllUsersRoute: Route = findAllUsersEndpoint.toRoute(_ => findAllUsers)

  // Выводим пользователя по id
  private def getUserEndpoint: Endpoint[UUID, String, User, Any] =
    baseEndpoint.get
      .description("Вывод конкретной пользователя по Id")
      .in(path[UUID])
      .out(jsonBody[User])

  private def getUser(id: UUID): Future[Either[String, User]] = {
    service.getUser(GetUserRequest(id)) map {
      case GetUserResponse.Found(user) => Right(user)
      case GetUserResponse.NotFound(_) => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def getUserRoute: Route = getUserEndpoint.toRoute(getUser)

  // Ищем пользователя по параметрам
  private def findUsersEndpoint: Endpoint[QueryParams, String, Seq[User], Any] =
    baseEndpoint.get
      .description(
        "Поиск пользователей по параметрам: " +
          "'username' - имя в системе, " +
          "'lastname' - фамилия " +
          "'roleId' - Id роли"
      )
      .in("find")
      .in(queryParams)
      .out(jsonBody[Seq[User]])

  private def findUsers(queryParams: QueryParams): Future[Either[String, Seq[User]]] = {
    val queryParamsMap: Map[String, String] = queryParams.toMap
    val queryParamsKeySet: Set[String]      = queryParamsMap.keySet

    if (queryParamsKeySet.contains("username"))
      service.findUser(FindUserRequest.ByUserName(queryParamsMap.getOrElse("username", ""))) map {
        case FindUserResponse.Result(user) => Right(user)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("lastname"))
      service.findUser(FindUserRequest.ByLastName(queryParamsMap.getOrElse("lastname", ""))) map {
        case FindUserResponse.Result(user) => Right(user)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("roleId"))
      service.findUser(
        FindUserRequest.ByRole(
          new Role(Some(UUID.fromString(queryParamsMap.getOrElse("roleId", ""))), "")
        )
      ) map {
        case FindUserResponse.Result(user) => Right(user)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else Future(Left(StatusCodes.BadRequest.defaultMessage))
  }

  private def findUsersRoute: Route = findUsersEndpoint.toRoute(findUsers)

  // Создаём пользователя
  private def createUserEndpoint: Endpoint[User, String, User, Any] =
    baseEndpoint.post
      .description("Создаём пользователя")
      .in(jsonBody[User])
      .out(jsonBody[User])

  private def createUser(user: User): Future[Either[String, User]] =
    service.createUser(CreateUserRequest(user)) map {
      case CreateUserResponse(response) => Right(response)
      case _                            => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def createUserRoute: Route = createUserEndpoint.toRoute(createUser)

  // Удаляем пользователя по Id
  private def deleteUserEndpoint(): Endpoint[UUID, String, User, Any] =
    baseEndpoint.delete
      .description("Удаляем пользователя по Id")
      .in(path[UUID])
      .out(jsonBody[User])

  private def deleteUser(id: UUID): Future[Either[String, User]] =
    service.deleteUser(DeleteUserRequest(id)) map {
      case DeleteUserResponse.Deleted(user) => Right(user)
      case DeleteUserResponse.NotFound(_)   => Left(StatusCodes.NotFound.defaultMessage)
    }

  private def deleteUserRoute(): Route = deleteUserEndpoint().toRoute(deleteUser)

  // Обновляем пользователя
  private def updateUserEndpoint(): Endpoint[User, String, User, Any] =
    baseEndpoint.put
      .description("Обновляем пользователя")
      .in(jsonBody[User])
      .out(jsonBody[User])

  private def updateUser(user: User): Future[Either[String, User]] =
    service.updateUser(UpdateUserRequest(user)) map {
      case UpdateUserResponse.Updated(user)           => Right(user)
      case UpdateUserResponse.NotFound(_)             => Left(StatusCodes.NotFound.defaultMessage)
      case UpdateUserResponse.CantUpdateUserWithoutId => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def updateUserRoute(): Route = updateUserEndpoint().toRoute(updateUser)
}

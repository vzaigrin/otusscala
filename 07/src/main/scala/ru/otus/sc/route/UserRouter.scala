package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.model.user._
import ru.otus.sc.service.UserService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.json.UserJsonProtocol._
import ru.otus.sc.model.role.Role

class UserRouter(service: UserService) extends BaseRouter {
  def route: Route =
    pathPrefix("user") {
      getUser ~
        findUserByUserName ~
        findUserByLastName ~
        findUserByRoleId ~
        findAllUsers ~
        createUser ~
        updateUser ~
        deleteUser
    }

  private def getUser: Route = {
    (get & parameter("id".as[UUID])) { uuid =>
      onSuccess(service.getUser(GetUserRequest(uuid))) {
        case GetUserResponse.Found(user) => complete(user)
        case GetUserResponse.NotFound(_) => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findUserByUserName: Route = {
    (get & parameter("username".as[String])) { userName =>
      onSuccess(service.findUser(FindUserRequest.ByUserName(userName))) {
        case FindUserResponse.Result(userSeq) => complete(userSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findUserByLastName: Route = {
    (get & parameter("lastname".as[String])) { lastName =>
      onSuccess(service.findUser(FindUserRequest.ByUserName(lastName))) {
        case FindUserResponse.Result(userSeq) => complete(userSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findUserByRoleId: Route = {
    (get & parameter("roleId".as[UUID])) { roleId =>
      onSuccess(service.findUser(FindUserRequest.ByRole(new Role(Some(roleId), "")))) {
        case FindUserResponse.Result(userSeq) => complete(userSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findAllUsers: Route =
    get {
      onSuccess(service.findUser(FindUserRequest.All())) {
        case FindUserResponse.Result(userSeq) => complete(userSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }

  private def createUser: Route =
    (post & entity(as[User])) { user =>
      onSuccess(service.createUser(CreateUserRequest(user))) { response =>
        complete(response.user)
      }
    }

  private def updateUser: Route =
    (put & entity(as[User])) { user =>
      onSuccess(service.updateUser(UpdateUserRequest(user))) {
        case UpdateUserResponse.Updated(user)           => complete(user)
        case UpdateUserResponse.NotFound(_)             => complete(StatusCodes.NotFound)
        case UpdateUserResponse.CantUpdateUserWithoutId => complete(StatusCodes.BadRequest)
      }
    }

  private def deleteUser: Route =
    (delete & path(JavaUUID.map(DeleteUserRequest))) { userIdRequest =>
      onSuccess(service.deleteUser(userIdRequest)) {
        case DeleteUserResponse.Deleted(user) => complete(user)
        case DeleteUserResponse.NotFound(_)   => complete(StatusCodes.NotFound)
      }
    }
}

package ru.otus.sc.service.impl

import ru.otus.sc.dao.UserDao
import ru.otus.sc.model.user._
import ru.otus.sc.service.UserService
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(dao: UserDao)(implicit ec: ExecutionContext) extends UserService {
  override def createUser(request: CreateUserRequest): Future[CreateUserResponse] =
    dao.createUser(request.user).map(CreateUserResponse)

  override def getUser(request: GetUserRequest): Future[GetUserResponse] =
    dao.getUser(request.id) map {
      case Some(user) => GetUserResponse.Found(user)
      case None       => GetUserResponse.NotFound(request.id)
    }

  override def updateUser(request: UpdateUserRequest): Future[UpdateUserResponse] =
    request.user.id match {
      case None => Future.successful(UpdateUserResponse.CantUpdateUserWithoutId)
      case Some(userId) =>
        dao.updateUser(request.user) map {
          case Some(user) => UpdateUserResponse.Updated(user)
          case None       => UpdateUserResponse.NotFound(userId)
        }
    }

  override def deleteUser(request: DeleteUserRequest): Future[DeleteUserResponse] =
    dao
      .deleteUser(request.id)
      .map {
        _.map(DeleteUserResponse.Deleted)
          .getOrElse(DeleteUserResponse.NotFound(request.id))
      }

  override def findUser(request: FindUserRequest): Future[FindUserResponse] =
    request match {
      case FindUserRequest.ByUserName(userName) =>
        dao.findByUserName(userName).map(FindUserResponse.Result)
      case FindUserRequest.ByLastName(lastName) =>
        dao.findByLastName(lastName).map(FindUserResponse.Result)
      case FindUserRequest.ByRole(role) => dao.findByRole(role).map(FindUserResponse.Result)
      case FindUserRequest.All()        => dao.findAll().map(FindUserResponse.Result)
    }

}

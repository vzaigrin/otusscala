package ru.otus.sc.service

import ru.otus.sc.model.user._
import scala.concurrent.Future

trait UserService {
  def createUser(request: CreateUserRequest): Future[CreateUserResponse]
  def getUser(request: GetUserRequest): Future[GetUserResponse]
  def updateUser(request: UpdateUserRequest): Future[UpdateUserResponse]
  def deleteUser(request: DeleteUserRequest): Future[DeleteUserResponse]
  def findUser(request: FindUserRequest): Future[FindUserResponse]
}

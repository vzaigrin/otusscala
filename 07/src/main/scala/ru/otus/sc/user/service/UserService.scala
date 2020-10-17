package ru.otus.sc.user.service

import ru.otus.sc.user.model.{
  CreateUserRequest,
  CreateUserResponse,
  DeleteUserRequest,
  DeleteUserResponse,
  FindUsersRequest,
  FindUsersResponse,
  GetUserRequest,
  GetUserResponse,
  UpdateUserRequest,
  UpdateUserResponse
}

trait UserService {
  def createUser(request: CreateUserRequest): CreateUserResponse
  def getUser(request: GetUserRequest): GetUserResponse
  def updateUser(request: UpdateUserRequest): UpdateUserResponse
  def deleteUser(request: DeleteUserRequest): DeleteUserResponse
  def findUsers(request: FindUsersRequest): FindUsersResponse
}

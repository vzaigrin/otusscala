package ru.otus.sc.model.role

sealed trait FindRoleRequest

object FindRoleRequest {
  case class ByName(name: String) extends FindRoleRequest
  case class All()                extends FindRoleRequest
}

sealed trait FindRoleResponse

object FindRoleResponse {
  case class Result(roles: Seq[Role]) extends FindRoleResponse
}

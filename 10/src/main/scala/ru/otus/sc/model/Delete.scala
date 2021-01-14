package ru.otus.sc.model

import java.util.UUID

case class DeleteRequest(id: UUID)

sealed trait DeleteResponse
object DeleteResponse {
  case class Deleted[+T <: Entity](entity: T) extends DeleteResponse
  case class NotFound(id: UUID)               extends DeleteResponse
}

package ru.otus.sc.model

import java.util.UUID

case class UpdateRequest[+T <: Entity](entity: T)

sealed trait UpdateResponse
object UpdateResponse {
  case class Updated[+T <: Entity](entity: T) extends UpdateResponse
  case class NotFound(id: UUID)               extends UpdateResponse
  case object CantUpdateWithoutId             extends UpdateResponse
}

package ru.otus.sc.model

import java.util.UUID

case class GetRequest(id: UUID)

sealed trait GetResponse
object GetResponse {
  case class Found[+T <: Entity](entity: T) extends GetResponse
  case class NotFound(id: UUID)             extends GetResponse
}

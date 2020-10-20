package ru.otus.sc.model.record

import java.util.UUID

case class GetRecordRequest(id: UUID)

sealed trait GetRecordResponse

object GetRecordResponse {
  case class Found(record: Record) extends GetRecordResponse
  case class NotFound(id: UUID)    extends GetRecordResponse
}

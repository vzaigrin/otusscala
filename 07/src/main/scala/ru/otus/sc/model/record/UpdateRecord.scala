package ru.otus.sc.model.record

import java.util.UUID

case class UpdateRecordRequest(record: Record)

sealed trait UpdateRecordResponse

object UpdateRecordResponse {
  case class Updated(record: Record)    extends UpdateRecordResponse
  case class NotFound(id: UUID)         extends UpdateRecordResponse
  case object CantUpdateRecordWithoutId extends UpdateRecordResponse
}

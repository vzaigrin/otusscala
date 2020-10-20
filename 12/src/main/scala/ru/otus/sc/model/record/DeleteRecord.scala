package ru.otus.sc.model.record

import java.util.UUID

case class DeleteRecordRequest(id: UUID)

sealed trait DeleteRecordResponse

object DeleteRecordResponse {
  case class Deleted(record: Record) extends DeleteRecordResponse
  case class NotFound(id: UUID)      extends DeleteRecordResponse
}

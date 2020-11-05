package ru.otus.sc.service

import ru.otus.sc.model.record._
import scala.concurrent.Future

trait RecordService {
  def createRecord(request: CreateRecordRequest): Future[CreateRecordResponse]
  def getRecord(request: GetRecordRequest): Future[GetRecordResponse]
  def updateRecord(request: UpdateRecordRequest): Future[UpdateRecordResponse]
  def deleteRecord(request: DeleteRecordRequest): Future[DeleteRecordResponse]
  def findRecord(request: FindRecordRequest): Future[FindRecordResponse]
}

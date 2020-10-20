package ru.otus.sc.service.impl

import ru.otus.sc.dao.RecordDao
import ru.otus.sc.model.record._
import ru.otus.sc.service.RecordService
import scala.concurrent.{ExecutionContext, Future}

class RecordServiceImpl(dao: RecordDao)(implicit ec: ExecutionContext) extends RecordService {
  override def createRecord(request: CreateRecordRequest): Future[CreateRecordResponse] =
    dao.createRecord(request.record).map(CreateRecordResponse)

  override def getRecord(request: GetRecordRequest): Future[GetRecordResponse] =
    dao.getRecord(request.id) map {
      case Some(record) => GetRecordResponse.Found(record)
      case None         => GetRecordResponse.NotFound(request.id)
    }

  override def updateRecord(request: UpdateRecordRequest): Future[UpdateRecordResponse] =
    request.record.id match {
      case None => Future.successful(UpdateRecordResponse.CantUpdateRecordWithoutId)
      case Some(recordId) =>
        dao.updateRecord(request.record) map {
          case Some(record) => UpdateRecordResponse.Updated(record)
          case None         => UpdateRecordResponse.NotFound(recordId)
        }
    }

  override def deleteRecord(request: DeleteRecordRequest): Future[DeleteRecordResponse] =
    dao
      .deleteRecord(request.id)
      .map {
        _.map(DeleteRecordResponse.Deleted)
          .getOrElse(DeleteRecordResponse.NotFound(request.id))
      }

  override def findRecord(request: FindRecordRequest): Future[FindRecordResponse] =
    request match {
      case FindRecordRequest.ByUser(user) => dao.findByUser(user).map(FindRecordResponse.Result)
      case FindRecordRequest.ByBook(book) => dao.findByBook(book).map(FindRecordResponse.Result)
      case FindRecordRequest.ByGet(dt)    => dao.findByGet(dt).map(FindRecordResponse.Result)
      case FindRecordRequest.ByReturn(dt) => dao.findByReturn(dt).map(FindRecordResponse.Result)
      case FindRecordRequest.All()        => dao.findAll().map(FindRecordResponse.Result)
    }

}

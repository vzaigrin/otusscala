package ru.otus.sc.service.impl

import ru.otus.sc.dao.Dao
import ru.otus.sc.model._
import ru.otus.sc.service.Service
import scala.concurrent.{ExecutionContext, Future}

class ServiceImpl[T <: Entity](dao: Dao[T])(implicit ec: ExecutionContext) extends Service[T] {
  override def create(request: CreateRequest[T]): Future[CreateResponse[T]] =
    dao.create(request.entity).map(e => CreateResponse(e))

  override def get(request: GetRequest): Future[GetResponse] =
    dao.get(request.id) map {
      case Some(entity) => GetResponse.Found(entity)
      case None         => GetResponse.NotFound(request.id)
    }

  override def update(request: UpdateRequest[T]): Future[UpdateResponse] =
    request.entity.id match {
      case None => Future.successful(UpdateResponse.CantUpdateWithoutId)
      case Some(id) =>
        dao.update(request.entity) map {
          case Some(entity) => UpdateResponse.Updated(entity)
          case None         => UpdateResponse.NotFound(id)
        }
    }

  override def delete(request: DeleteRequest): Future[DeleteResponse] =
    dao
      .delete(request.id)
      .map {
        _.map(e => DeleteResponse.Deleted(e))
          .getOrElse(DeleteResponse.NotFound(request.id))
      }

  override def find(request: FindRequest): Future[FindResponse] =
    request match {
      case FindRequest.ByField(field, value) =>
        dao.findByField(field, value).map(r => FindResponse.Result(r))
      case FindRequest.All => dao.findAll().map(r => FindResponse.Result(r))
    }
}

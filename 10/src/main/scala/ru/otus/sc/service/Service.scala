package ru.otus.sc.service

import ru.otus.sc.model._
import scala.concurrent.Future

trait Service[T <: Entity] {
  def create(request: CreateRequest[T]): Future[CreateResponse[T]]
  def get(request: GetRequest): Future[GetResponse]
  def update(request: UpdateRequest[T]): Future[UpdateResponse]
  def delete(request: DeleteRequest): Future[DeleteResponse]
  def find(request: FindRequest): Future[FindResponse]
}

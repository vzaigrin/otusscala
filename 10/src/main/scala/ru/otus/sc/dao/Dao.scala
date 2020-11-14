package ru.otus.sc.dao

import java.util.UUID
import ru.otus.sc.model._
import scala.concurrent.Future

trait Dao[T <: Entity] {
  def init(): Future[Unit]
  def clean(): Future[Unit]
  def destroy(): Future[Unit]
  def create(entity: T): Future[T]
  def get(id: UUID): Future[Option[T]]
  def update(entity: T): Future[Option[T]]
  def delete(id: UUID): Future[Option[T]]
  def findByField(field: String, value: String): Future[Seq[T]]
  def findAll(): Future[Seq[T]]
}

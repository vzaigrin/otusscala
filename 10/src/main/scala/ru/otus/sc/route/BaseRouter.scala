package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.server.Route
import ru.otus.sc.model._
import ru.otus.sc.service.Service
import sttp.tapir.{Endpoint, endpoint}
import scala.concurrent.{ExecutionContextExecutor, Future}

sealed trait ErrorInfo
case class NotFound(what: String)      extends ErrorInfo
case class Unauthorized(realm: String) extends ErrorInfo
case class BadRequest(what: String)    extends ErrorInfo

abstract class BaseRouter[T <: Entity](pathPrefix: String, service: Service[T]) {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def endpoints: List[Endpoint[_, _, _, _]]
  def route: Route

  val startEndpoint: Endpoint[Unit, Unit, Unit, Any] = endpoint.in(pathPrefix)

  def create(e: T): Future[Either[ErrorInfo, T]] =
    service.create(CreateRequest(e)) map {
      case CreateResponse(e) => Right(e)
      case _                 => Left(BadRequest(e.toString))
    }

  def get(id: UUID): Future[Either[ErrorInfo, T]] = {
    service.get(GetRequest(id)) map {
      case GetResponse.Found(e)     => Right(e.asInstanceOf[T])
      case GetResponse.NotFound(id) => Left(NotFound(id.toString))
    }
  }

  def find(qSeq: Seq[(String, Option[String])]): Future[Either[ErrorInfo, Seq[T]]] = {
    qSeq.headOption match {
      case Some(key) =>
        service.find(FindRequest.ByField(key._1, key._2.get)) map {
          case FindResponse.Result(e) => Right(e.map(_.asInstanceOf[T]))
          case _                      => Left(NotFound(key._1))
        }
      case None =>
        service.find(FindRequest.All) map {
          case FindResponse.Result(e) => Right(e.map(_.asInstanceOf[T]))
          case _                      => Left(NotFound("all"))
        }
    }
  }

  /*
    def findAll: Future[Either[ErrorInfo, Seq[T]]] = {
      service.find(FindRequest.All) map {
        case FindResponse.Result(e) => Right(e.map(_.asInstanceOf[T]))
        case _                      => Left(NotFound("find all"))
      }
    }

    def findByParams(qSeq: Seq[(String, Option[String])]): Future[Either[ErrorInfo, Seq[T]]] = {
      qSeq.headOption match {
        case Some(key) =>
          service.find(FindRequest.ByField(key._1, key._2.get)) map {
            case FindResponse.Result(e) => Right(e.map(_.asInstanceOf[T]))
            case _                      => Left(NotFound(key._1))
          }
        case None => Future.successful(Left(NotFound("find by parameter")))
      }
    }
   */

  def update(e: T): Future[Either[ErrorInfo, T]] =
    service.update(UpdateRequest(e)) map {
      case UpdateResponse.Updated(e)          => Right(e.asInstanceOf[T])
      case UpdateResponse.NotFound(_)         => Left(NotFound(e.toString))
      case UpdateResponse.CantUpdateWithoutId => Left(BadRequest(e.toString))
    }

  def delete(id: UUID): Future[Either[ErrorInfo, T]] =
    service.delete(DeleteRequest(id)) map {
      case DeleteResponse.Deleted(e)  => Right(e.asInstanceOf[T])
      case DeleteResponse.NotFound(_) => Left(NotFound(id.toString))
    }
}

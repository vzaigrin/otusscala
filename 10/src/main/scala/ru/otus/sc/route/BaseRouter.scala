package ru.otus.sc.route

import java.util.UUID
import akka.actor.typed.{ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import ru.otus.sc.auth.model._
import ru.otus.sc.model._
import ru.otus.sc.service.Service
import sttp.tapir.TapirAuth.bearer
import sttp.tapir.{Endpoint, endpoint}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import sttp.tapir.json.circe.jsonBody
import io.circe.generic.auto._

abstract class BaseRouter[T <: Entity](
    pathPrefix: String,
    service: Service[T],
    authSystem: ActorSystem[AuthRequest]
) {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  implicit val timeout: Timeout             = 10.seconds
  implicit val scheduler: Scheduler         = authSystem.scheduler

  def endpoints: List[Endpoint[_, _, _, _]]
  def route: Route

  private var result: Future[Either[ErrorInfo, AuthToken]] = _

  val startEndpoint: Endpoint[String, ErrorInfo, Unit, Any] =
    endpoint
      .in(pathPrefix)
      .in(bearer[String])
      .errorOut(jsonBody[ErrorInfo])

  def auth(token: String): Future[Either[ErrorInfo, AuthToken]] = {
    authSystem.ask(ref => CheckToken(token, ref)).onComplete {
      case Success(TokenValid(token))   => result = Future(Right(AuthToken(token)))
      case Success(TokenInvalid(token)) => result = Future(Left(Unauthorized(token)))
      case Failure(exception)           => result = Future(Left(BadRequest(exception.toString)))
      case _                            => result = Future(Left(BadRequest(token)))
    }
    result
  }

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

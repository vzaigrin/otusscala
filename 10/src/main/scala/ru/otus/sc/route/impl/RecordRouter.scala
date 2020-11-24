package ru.otus.sc.route.impl

import java.util.UUID
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import ru.otus.sc.auth.model.AuthRequest
import ru.otus.sc.model._
import ru.otus.sc.route._
import ru.otus.sc.service.Service
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.akkahttp.RichAkkaHttpServerEndpoint
import scala.concurrent.Future

class RecordRouter(
    pathPrefix: String,
    service: Service[Record],
    authSystem: ActorSystem[AuthRequest]
) extends BaseRouter(
      pathPrefix: String,
      service: Service[Record],
      authSystem: ActorSystem[AuthRequest]
    ) {
  private val pathSuffix: String = "record"

  override def endpoints: List[Endpoint[_, _, _, _]] =
    List(
      createEndpoint,
      getEndpoint,
      findAllEndpoint,
      findByFieldEndpoint,
      updateEndpoint(),
      deleteEndpoint()
    )

  override def route: Route =
    concat(createRoute, getRoute, findAllRoute, findByFieldRoute, updateRoute(), deleteRoute())

  private val baseEndpoint: Endpoint[String, ErrorInfo, Unit, Any] =
    startEndpoint
      .tag(pathSuffix)
      .in(pathSuffix)

  // Создаём сущность
  def createEndpoint: Endpoint[(String, Record), ErrorInfo, Record, Any] =
    baseEndpoint.post
      .description("Создаём запись")
      .in(jsonBody[Record])
      .out(jsonBody[Record])

  val createEndpointWithLogic: ServerEndpoint[(String, Record), ErrorInfo, Record, Any, Future] =
    createEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, record) => create(record) }

  def createRoute: Route = createEndpointWithLogic.toRoute

  // Выводим сущность по id
  def getEndpoint: Endpoint[(String, UUID), ErrorInfo, Record, Any] =
    baseEndpoint.get
      .description("Вывод записи по Id")
      .in(path[UUID])
      .out(jsonBody[Record])

  val getEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Record, Any, Future] =
    getEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, id) => get(id) }

  def getRoute: Route = getEndpointWithLogic.toRoute

  // Выводим все сущности
  def findAllEndpoint: Endpoint[String, ErrorInfo, Seq[Record], Any] =
    baseEndpoint.get
      .description("Вывод всех записей")
      .out(jsonBody[Seq[Record]])

  val findAllEndpointWithLogic: ServerEndpoint[String, ErrorInfo, Seq[Record], Any, Future] =
    findAllEndpoint
      .serverLogicPart(auth)
      .andThen { _ => find(Seq()) }

  def findAllRoute: Route = findAllEndpointWithLogic.toRoute

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[
    (String, Option[String], Option[String], Option[String], Option[String]),
    ErrorInfo,
    Seq[Record],
    Any
  ] =
    baseEndpoint.get
      .description(
        "Вывод всех записей или поиск по параметрам: " +
          "'user' - Id пользователя, " +
          "'book' - Id книги, " +
          "'get' - yyyyMMddTHHmmss когда книгу взяли, " +
          "'return' - yyyyMMddTHHmmss когда книгу вернули"
      )
      .in("find")
      .in(query[Option[String]]("user"))
      .in(query[Option[String]]("book"))
      .in(query[Option[String]]("get"))
      .in(query[Option[String]]("return"))
      .out(jsonBody[Seq[Record]])

  val findByFieldEndpointWithLogic: ServerEndpoint[
    (String, Option[String], Option[String], Option[String], Option[String]),
    ErrorInfo,
    Seq[Record],
    Any,
    Future
  ] =
    findByFieldEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, query) => findByField(query) }

  def findByField(
      query: (Option[String], Option[String], Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[Record]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("user", query._1), ("book", query._2), ("get", query._3), ("return", query._4))
        .filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpointWithLogic.toRoute

  // Обновляем сущность
  def updateEndpoint(): Endpoint[(String, Record), ErrorInfo, Record, Any] =
    baseEndpoint.put
      .description("Обновляем запись")
      .in(jsonBody[Record])
      .out(jsonBody[Record])

  val updateEndpointWithLogic: ServerEndpoint[(String, Record), ErrorInfo, Record, Any, Future] =
    updateEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, record) => update(record) }

  def updateRoute(): Route = updateEndpointWithLogic.toRoute

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[(String, UUID), ErrorInfo, Record, Any] =
    baseEndpoint.delete
      .description("Удаляем запись по Id")
      .in(path[UUID])
      .out(jsonBody[Record])

  val deleteEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Record, Any, Future] =
    deleteEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, id) => delete(id) }

  def deleteRoute(): Route = deleteEndpointWithLogic.toRoute
}

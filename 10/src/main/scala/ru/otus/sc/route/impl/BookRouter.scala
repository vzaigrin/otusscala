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

class BookRouter(pathPrefix: String, service: Service[Book], authSystem: ActorSystem[AuthRequest])
    extends BaseRouter(
      pathPrefix: String,
      service: Service[Book],
      authSystem: ActorSystem[AuthRequest]
    ) {
  private val pathSuffix: String = "book"

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
  def createEndpoint: Endpoint[(String, Book), ErrorInfo, Book, Any] =
    baseEndpoint.post
      .description("Создаём книгу")
      .in(jsonBody[Book])
      .out(jsonBody[Book])

  val createEndpointWithLogic: ServerEndpoint[(String, Book), ErrorInfo, Book, Any, Future] =
    createEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, book) => create(book) }

  def createRoute: Route = createEndpointWithLogic.toRoute

  // Выводим сущность по id
  def getEndpoint: Endpoint[(String, UUID), ErrorInfo, Book, Any] =
    baseEndpoint.get
      .description("Вывод книги по Id")
      .in(path[UUID])
      .out(jsonBody[Book])

  val getEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Book, Any, Future] =
    getEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, id) => get(id) }

  def getRoute: Route = getEndpointWithLogic.toRoute

  // Выводим все сущности
  def findAllEndpoint: Endpoint[String, ErrorInfo, Seq[Book], Any] =
    baseEndpoint.get
      .description("Вывод всех книг")
      .out(jsonBody[Seq[Book]])

  val findAllEndpointWithLogic: ServerEndpoint[String, ErrorInfo, Seq[Book], Any, Future] =
    findAllEndpoint
      .serverLogicPart(auth)
      .andThen { _ => find(Seq()) }

  def findAllRoute: Route = findAllEndpointWithLogic.toRoute

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[
    (String, Option[String], Option[String], Option[String], Option[String]),
    ErrorInfo,
    Seq[Book],
    Any
  ] =
    baseEndpoint.get
      .description(
        "Вывод всех книг или поиск по параметрам: " +
          "'title' - название, " +
          "'author' - Id автора, " +
          "'published' - год издания, " +
          "'pages' - кол-во страниц"
      )
      .in("find")
      .in(query[Option[String]]("title"))
      .in(query[Option[String]]("author"))
      .in(query[Option[String]]("published"))
      .in(query[Option[String]]("pages"))
      .out(jsonBody[Seq[Book]])

  val findByFieldEndpointWithLogic: ServerEndpoint[
    (String, Option[String], Option[String], Option[String], Option[String]),
    ErrorInfo,
    Seq[Book],
    Any,
    Future
  ] =
    findByFieldEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, query) => findByField(query) }

  def findByField(
      query: (Option[String], Option[String], Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[Book]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("title", query._1), ("author", query._2), ("published", query._3), ("pages", query._4))
        .filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpointWithLogic.toRoute

  // Обновляем сущность
  def updateEndpoint(): Endpoint[(String, Book), ErrorInfo, Book, Any] =
    baseEndpoint.put
      .description("Обновляем книгу")
      .in(jsonBody[Book])
      .out(jsonBody[Book])

  val updateEndpointWithLogic: ServerEndpoint[(String, Book), ErrorInfo, Book, Any, Future] =
    updateEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, book) => update(book) }

  def updateRoute(): Route = updateEndpointWithLogic.toRoute

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[(String, UUID), ErrorInfo, Book, Any] =
    baseEndpoint.delete
      .description("Удаляем книгу по Id")
      .in(path[UUID])
      .out(jsonBody[Book])

  val deleteEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Book, Any, Future] =
    deleteEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, id) => delete(id) }

  def deleteRoute(): Route = deleteEndpointWithLogic.toRoute
}

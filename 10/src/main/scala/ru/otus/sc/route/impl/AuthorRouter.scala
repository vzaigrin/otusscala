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

class AuthorRouter(
    pathPrefix: String,
    service: Service[Author],
    authSystem: ActorSystem[AuthRequest]
) extends BaseRouter(
      pathPrefix: String,
      service: Service[Author],
      authSystem: ActorSystem[AuthRequest]
    ) {
  private val pathSuffix: String = "author"

  def endpoints =
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
  def createEndpoint: Endpoint[(String, Author), ErrorInfo, Author, Any] =
    baseEndpoint.post
      .description("Создаём автора")
      .in(jsonBody[Author])
      .out(jsonBody[Author])

  val createEndpointWithLogic: ServerEndpoint[(String, Author), ErrorInfo, Author, Any, Future] =
    createEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, author) => create(author) }

  def createRoute: Route = createEndpointWithLogic.toRoute

  // Выводим сущность по id
  def getEndpoint: Endpoint[(String, UUID), ErrorInfo, Author, Any] =
    baseEndpoint.get
      .description("Вывод автора по Id")
      .in(path[UUID])
      .out(jsonBody[Author])

  val getEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Author, Any, Future] =
    getEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, id) => get(id) }

  def getRoute: Route = getEndpointWithLogic.toRoute

  // Выводим все сущности
  def findAllEndpoint: Endpoint[String, ErrorInfo, Seq[Author], Any] =
    baseEndpoint.get
      .description("Вывод всех авторов")
      .out(jsonBody[Seq[Author]])

  val findAllEndpointWithLogic: ServerEndpoint[String, ErrorInfo, Seq[Author], Any, Future] =
    findAllEndpoint
      .serverLogicPart(auth)
      .andThen { _ => find(Seq()) }

  def findAllRoute: Route = findAllEndpointWithLogic.toRoute

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint
      : Endpoint[(String, Option[String], Option[String]), ErrorInfo, Seq[Author], Any] =
    baseEndpoint.get
      .description(
        "Вывод всех авторов или поиск по параметрам: " + "'firstname' - имя, " + "'lastname' - фамилия"
      )
      .in("find")
      .in(query[Option[String]]("firstname"))
      .in(query[Option[String]]("lastname"))
      .out(jsonBody[Seq[Author]])

  val findByFieldEndpointWithLogic
      : ServerEndpoint[(String, Option[String], Option[String]), ErrorInfo, Seq[
        Author
      ], Any, Future] =
    findByFieldEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, query) => findByField(query) }

  def findByField(
      query: (Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[Author]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("firstname", query._1), ("lastname", query._2)).filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpointWithLogic.toRoute

  // Обновляем сущность
  def updateEndpoint(): Endpoint[(String, Author), ErrorInfo, Author, Any] =
    baseEndpoint.put
      .description("Обновляем автора")
      .in(jsonBody[Author])
      .out(jsonBody[Author])

  val updateEndpointWithLogic: ServerEndpoint[(String, Author), ErrorInfo, Author, Any, Future] =
    updateEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, author) => update(author) }

  def updateRoute(): Route = updateEndpointWithLogic.toRoute

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[(String, UUID), ErrorInfo, Author, Any] =
    baseEndpoint.delete
      .description("Удаляем автора по Id")
      .in(path[UUID])
      .out(jsonBody[Author])

  val deleteEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Author, Any, Future] =
    deleteEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, id) => delete(id) }

  def deleteRoute(): Route = deleteEndpointWithLogic.toRoute
}

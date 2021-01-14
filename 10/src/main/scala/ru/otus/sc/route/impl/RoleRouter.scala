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

class RoleRouter(pathPrefix: String, service: Service[Role], authSystem: ActorSystem[AuthRequest])
    extends BaseRouter(
      pathPrefix: String,
      service: Service[Role],
      authSystem: ActorSystem[AuthRequest]
    ) {
  private val pathSuffix: String = "role"

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
  def createEndpoint: Endpoint[(String, Role), ErrorInfo, Role, Any] =
    baseEndpoint.post
      .description("Создаём роль")
      .in(jsonBody[Role])
      .out(jsonBody[Role])

  val createEndpointWithLogic: ServerEndpoint[(String, Role), ErrorInfo, Role, Any, Future] =
    createEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, role) => create(role) }

  def createRoute: Route = createEndpointWithLogic.toRoute

  // Выводим сущность по id
  def getEndpoint: Endpoint[(String, UUID), ErrorInfo, Role, Any] =
    baseEndpoint.get
      .description("Вывод роли по Id")
      .in(path[UUID])
      .out(jsonBody[Role])

  val getEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Role, Any, Future] =
    getEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, id) => get(id) }

  def getRoute: Route = getEndpointWithLogic.toRoute

  // Выводим все сущности
  def findAllEndpoint: Endpoint[String, ErrorInfo, Seq[Role], Any] =
    baseEndpoint.get
      .description("Вывод всех ролей")
      .out(jsonBody[Seq[Role]])

  val findAllEndpointWithLogic: ServerEndpoint[String, ErrorInfo, Seq[Role], Any, Future] =
    findAllEndpoint
      .serverLogicPart(auth)
      .andThen { _ => find(Seq()) }

  def findAllRoute: Route = findAllEndpointWithLogic.toRoute

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[(String, Option[String]), ErrorInfo, Seq[Role], Any] =
    baseEndpoint.get
      .description("Вывод всех ролей или поиск по параметру: 'name' - имя")
      .in("find")
      .in(query[Option[String]]("name"))
      .out(jsonBody[Seq[Role]])

  val findByFieldEndpointWithLogic
      : ServerEndpoint[(String, Option[String]), ErrorInfo, Seq[Role], Any, Future] =
    findByFieldEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, query) => findByField(query) }

  def findByField(query: Option[String]): Future[Either[ErrorInfo, Seq[Role]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("name", query)).filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpointWithLogic.toRoute

  // Обновляем сущность
  def updateEndpoint(): Endpoint[(String, Role), ErrorInfo, Role, Any] =
    baseEndpoint.put
      .description("Обновляем роль")
      .in(jsonBody[Role])
      .out(jsonBody[Role])

  val updateEndpointWithLogic: ServerEndpoint[(String, Role), ErrorInfo, Role, Any, Future] =
    updateEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, role) => update(role) }

  def updateRoute(): Route = updateEndpointWithLogic.toRoute

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[(String, UUID), ErrorInfo, Role, Any] =
    baseEndpoint.delete
      .description("Удаляем роль по Id")
      .in(path[UUID])
      .out(jsonBody[Role])

  val deleteEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, Role, Any, Future] =
    deleteEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, id) => delete(id) }

  def deleteRoute(): Route = deleteEndpointWithLogic.toRoute
}

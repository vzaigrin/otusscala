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

class UserRouter(pathPrefix: String, service: Service[User], authSystem: ActorSystem[AuthRequest])
    extends BaseRouter(
      pathPrefix: String,
      service: Service[User],
      authSystem: ActorSystem[AuthRequest]
    ) {
  private val pathSuffix: String = "user"

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
  def createEndpoint: Endpoint[(String, User), ErrorInfo, User, Any] =
    baseEndpoint.post
      .description("Создаём пользователя")
      .in(jsonBody[User])
      .out(jsonBody[User])

  val createEndpointWithLogic: ServerEndpoint[(String, User), ErrorInfo, User, Any, Future] =
    createEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, user) => create(user) }

  def createRoute: Route = createEndpointWithLogic.toRoute

  // Выводим сущность по id
  def getEndpoint: Endpoint[(String, UUID), ErrorInfo, User, Any] =
    baseEndpoint.get
      .description("Вывод пользователя по Id")
      .in(path[UUID])
      .out(jsonBody[User])

  val getEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, User, Any, Future] =
    getEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, id) => get(id) }

  def getRoute: Route = getEndpointWithLogic.toRoute

  // Выводим все сущности
  def findAllEndpoint: Endpoint[String, ErrorInfo, Seq[User], Any] =
    baseEndpoint.get
      .description("Вывод всех пользователей")
      .out(jsonBody[Seq[User]])

  val findAllEndpointWithLogic: ServerEndpoint[String, ErrorInfo, Seq[User], Any, Future] =
    findAllEndpoint
      .serverLogicPart(auth)
      .andThen { _ => find(Seq()) }

  def findAllRoute: Route = findAllEndpointWithLogic.toRoute

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[
    (String, Option[String], Option[String], Option[String], Option[String], Option[String]),
    ErrorInfo,
    Seq[User],
    Any
  ] =
    baseEndpoint.get
      .description(
        "Вывод всех пользователей или поиск по параметрам: " +
          "'username' - имя в системе, " +
          "'firstname' - имя " +
          "'lastname' - фамилия " +
          "'age' - возраст" +
          "'role' - Id роли"
      )
      .in("find")
      .in(query[Option[String]]("username"))
      .in(query[Option[String]]("firstname"))
      .in(query[Option[String]]("lastname"))
      .in(query[Option[String]]("age"))
      .in(query[Option[String]]("role"))
      .out(jsonBody[Seq[User]])

  val findByFieldEndpointWithLogic: ServerEndpoint[
    (String, Option[String], Option[String], Option[String], Option[String], Option[String]),
    ErrorInfo,
    Seq[User],
    Any,
    Future
  ] =
    findByFieldEndpoint
      .serverLogicPart(auth)
      .andThen { case (_, query) => findByField(query) }

  def findByField(
      query: (Option[String], Option[String], Option[String], Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[User]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(
        ("username", query._1),
        ("firstname", query._2),
        ("lastname", query._3),
        ("age", query._4),
        ("role", query._5)
      ).filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpointWithLogic.toRoute

  // Обновляем сущность
  def updateEndpoint(): Endpoint[(String, User), ErrorInfo, User, Any] =
    baseEndpoint.put
      .description("Обновляем пользователя")
      .in(jsonBody[User])
      .out(jsonBody[User])

  val updateEndpointWithLogic: ServerEndpoint[(String, User), ErrorInfo, User, Any, Future] =
    updateEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, user) => update(user) }

  def updateRoute(): Route = updateEndpointWithLogic.toRoute

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[(String, UUID), ErrorInfo, User, Any] =
    baseEndpoint.delete
      .description("Удаляем пользователя по Id")
      .in(path[UUID])
      .out(jsonBody[User])

  val deleteEndpointWithLogic: ServerEndpoint[(String, UUID), ErrorInfo, User, Any, Future] =
    deleteEndpoint()
      .serverLogicPart(auth)
      .andThen { case (_, id) => delete(id) }

  def deleteRoute(): Route = deleteEndpointWithLogic.toRoute
}

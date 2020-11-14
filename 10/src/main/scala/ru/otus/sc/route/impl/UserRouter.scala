package ru.otus.sc.route.impl

import java.util.UUID
import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import ru.otus.sc.model._
import ru.otus.sc.route._
import ru.otus.sc.service.Service
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import scala.concurrent.Future

class UserRouter(pathPrefix: String, service: Service[User])
    extends BaseRouter(pathPrefix: String, service: Service[User]) {
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

  private val baseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Any] =
    startEndpoint
      .tag(pathSuffix)
      .in(pathSuffix)
      .errorOut(jsonBody[ErrorInfo])

  // Создаём сущность
  def createEndpoint: Endpoint[User, ErrorInfo, User, Any] =
    baseEndpoint.post
      .description("Создаём пользователя")
      .in(jsonBody[User])
      .out(jsonBody[User])

  def createRoute: Route = createEndpoint.toRoute(create)

  // Выводим сущность по id
  def getEndpoint: Endpoint[UUID, ErrorInfo, User, Any] =
    baseEndpoint.get
      .description("Вывод пользователя по Id")
      .in(path[UUID])
      .out(jsonBody[User])

  def getRoute: Route = getEndpoint.toRoute(get)

  // Выводим все сущности
  def findAllEndpoint: Endpoint[Unit, ErrorInfo, Seq[User], Any] =
    baseEndpoint.get
      .description("Вывод всех пользователей")
      .out(jsonBody[Seq[User]])

  def findAllRoute: Route = findAllEndpoint.toRoute(_ => find(Seq()))

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[
    (Option[String], Option[String], Option[String], Option[String], Option[String]),
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

  def findByFieldRoute: Route = findByFieldEndpoint.toRoute(findByField)

  // Обновляем сущность
  def updateEndpoint(): Endpoint[User, ErrorInfo, User, Any] =
    baseEndpoint.put
      .description("Обновляем пользователя")
      .in(jsonBody[User])
      .out(jsonBody[User])

  def updateRoute(): Route = updateEndpoint().toRoute(update)

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[UUID, ErrorInfo, User, Any] =
    baseEndpoint.delete
      .description("Удаляем пользователя по Id")
      .in(path[UUID])
      .out(jsonBody[User])

  def deleteRoute(): Route = deleteEndpoint().toRoute(delete)
}

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

class RoleRouter(pathPrefix: String, service: Service[Role])
    extends BaseRouter(pathPrefix: String, service: Service[Role]) {
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

  private val baseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Any] =
    startEndpoint
      .tag(pathSuffix)
      .in(pathSuffix)
      .errorOut(jsonBody[ErrorInfo])

  // Создаём сущность
  def createEndpoint: Endpoint[Role, ErrorInfo, Role, Any] =
    baseEndpoint.post
      .description("Создаём роль")
      .in(jsonBody[Role])
      .out(jsonBody[Role])

  def createRoute: Route = createEndpoint.toRoute(create)

  // Выводим сущность по id
  def getEndpoint: Endpoint[UUID, ErrorInfo, Role, Any] =
    baseEndpoint.get
      .description("Вывод роли по Id")
      .in(path[UUID])
      .out(jsonBody[Role])

  def getRoute: Route = getEndpoint.toRoute(get)

  // Выводим все сущности
  def findAllEndpoint: Endpoint[Unit, ErrorInfo, Seq[Role], Any] =
    baseEndpoint.get
      .description("Вывод всех ролей")
      .out(jsonBody[Seq[Role]])

  def findAllRoute: Route = findAllEndpoint.toRoute(_ => find(Seq()))

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[Option[String], ErrorInfo, Seq[Role], Any] =
    baseEndpoint.get
      .description("Вывод всех ролей или поиск по параметру: 'name' - имя")
      .in("find")
      .in(query[Option[String]]("name"))
      .out(jsonBody[Seq[Role]])

  def findByField(query: Option[String]): Future[Either[ErrorInfo, Seq[Role]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("name", query)).filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpoint.toRoute(findByField)

  // Обновляем сущность
  def updateEndpoint(): Endpoint[Role, ErrorInfo, Role, Any] =
    baseEndpoint.put
      .description("Обновляем роль")
      .in(jsonBody[Role])
      .out(jsonBody[Role])

  def updateRoute(): Route = updateEndpoint().toRoute(update)

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[UUID, ErrorInfo, Role, Any] =
    baseEndpoint.delete
      .description("Удаляем роль по Id")
      .in(path[UUID])
      .out(jsonBody[Role])

  def deleteRoute(): Route = deleteEndpoint().toRoute(delete)
}

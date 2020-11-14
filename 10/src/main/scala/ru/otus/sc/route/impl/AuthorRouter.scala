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

class AuthorRouter(pathPrefix: String, service: Service[Author])
    extends BaseRouter(pathPrefix: String, service: Service[Author]) {
  private val pathSuffix: String = "author"

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
  def createEndpoint: Endpoint[Author, ErrorInfo, Author, Any] =
    baseEndpoint.post
      .description("Создаём автора")
      .in(jsonBody[Author])
      .out(jsonBody[Author])

  def createRoute: Route = createEndpoint.toRoute(create)

  // Выводим сущность по id
  def getEndpoint: Endpoint[UUID, ErrorInfo, Author, Any] =
    baseEndpoint.get
      .description("Вывод автора по Id")
      .in(path[UUID])
      .out(jsonBody[Author])

  def getRoute: Route = getEndpoint.toRoute(get)

  // Выводим все сущности
  def findAllEndpoint: Endpoint[Unit, ErrorInfo, Seq[Author], Any] =
    baseEndpoint.get
      .description("Вывод всех авторов")
      .out(jsonBody[Seq[Author]])

  def findAllRoute: Route = findAllEndpoint.toRoute(_ => find(Seq()))

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint: Endpoint[(Option[String], Option[String]), ErrorInfo, Seq[Author], Any] =
    baseEndpoint.get
      .description(
        "Вывод всех авторов или поиск по параметрам: " + "'firstname' - имя, " + "'lastname' - фамилия"
      )
      .in("find")
      .in(query[Option[String]]("firstname"))
      .in(query[Option[String]]("lastname"))
      .out(jsonBody[Seq[Author]])

  def findByField(
      query: (Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[Author]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("firstname", query._1), ("lastname", query._2)).filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpoint.toRoute(findByField)

  // Обновляем сущность
  def updateEndpoint(): Endpoint[Author, ErrorInfo, Author, Any] =
    baseEndpoint.put
      .description("Обновляем автора")
      .in(jsonBody[Author])
      .out(jsonBody[Author])

  def updateRoute(): Route = updateEndpoint().toRoute(update)

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[UUID, ErrorInfo, Author, Any] =
    baseEndpoint.delete
      .description("Удаляем автора по Id")
      .in(path[UUID])
      .out(jsonBody[Author])

  def deleteRoute(): Route = deleteEndpoint().toRoute(delete)
}

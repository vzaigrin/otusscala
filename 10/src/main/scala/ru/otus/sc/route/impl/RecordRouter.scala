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

class RecordRouter(pathPrefix: String, service: Service[Record])
    extends BaseRouter(pathPrefix: String, service: Service[Record]) {
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

  private val baseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Any] =
    startEndpoint
      .tag(pathSuffix)
      .in(pathSuffix)
      .errorOut(jsonBody[ErrorInfo])

  // Создаём сущность
  def createEndpoint: Endpoint[Record, ErrorInfo, Record, Any] =
    baseEndpoint.post
      .description("Создаём запись")
      .in(jsonBody[Record])
      .out(jsonBody[Record])

  def createRoute: Route = createEndpoint.toRoute(create)

  // Выводим сущность по id
  def getEndpoint: Endpoint[UUID, ErrorInfo, Record, Any] =
    baseEndpoint.get
      .description("Вывод записи по Id")
      .in(path[UUID])
      .out(jsonBody[Record])

  def getRoute: Route = getEndpoint.toRoute(get)

  // Выводим все сущности
  def findAllEndpoint: Endpoint[Unit, ErrorInfo, Seq[Record], Any] =
    baseEndpoint.get
      .description("Вывод всех записей")
      .out(jsonBody[Seq[Record]])

  def findAllRoute: Route = findAllEndpoint.toRoute(_ => find(Seq()))

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint
      : Endpoint[(Option[String], Option[String], Option[String], Option[String]), ErrorInfo, Seq[
        Record
      ], Any] =
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

  def findByField(
      query: (Option[String], Option[String], Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[Record]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("user", query._1), ("book", query._2), ("get", query._3), ("return", query._4))
        .filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpoint.toRoute(findByField)

  // Обновляем сущность
  def updateEndpoint(): Endpoint[Record, ErrorInfo, Record, Any] =
    baseEndpoint.put
      .description("Обновляем запись")
      .in(jsonBody[Record])
      .out(jsonBody[Record])

  def updateRoute(): Route = updateEndpoint().toRoute(update)

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[UUID, ErrorInfo, Record, Any] =
    baseEndpoint.delete
      .description("Удаляем запись по Id")
      .in(path[UUID])
      .out(jsonBody[Record])

  def deleteRoute(): Route = deleteEndpoint().toRoute(delete)
}

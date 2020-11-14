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

class BookRouter(pathPrefix: String, service: Service[Book])
    extends BaseRouter(pathPrefix: String, service: Service[Book]) {
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

  private val baseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Any] =
    startEndpoint
      .tag(pathSuffix)
      .in(pathSuffix)
      .errorOut(jsonBody[ErrorInfo])

  // Создаём сущность
  def createEndpoint: Endpoint[Book, ErrorInfo, Book, Any] =
    baseEndpoint.post
      .description("Создаём книгу")
      .in(jsonBody[Book])
      .out(jsonBody[Book])

  def createRoute: Route = createEndpoint.toRoute(create)

  // Выводим сущность по id
  def getEndpoint: Endpoint[UUID, ErrorInfo, Book, Any] =
    baseEndpoint.get
      .description("Вывод книги по Id")
      .in(path[UUID])
      .out(jsonBody[Book])

  def getRoute: Route = getEndpoint.toRoute(get)

  // Выводим все сущности
  def findAllEndpoint: Endpoint[Unit, ErrorInfo, Seq[Book], Any] =
    baseEndpoint.get
      .description("Вывод всех книг")
      .out(jsonBody[Seq[Book]])

  def findAllRoute: Route = findAllEndpoint.toRoute(_ => find(Seq()))

  // Выводим все сущности или ищем по параметрам
  def findByFieldEndpoint
      : Endpoint[(Option[String], Option[String], Option[String], Option[String]), ErrorInfo, Seq[
        Book
      ], Any] =
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

  def findByField(
      query: (Option[String], Option[String], Option[String], Option[String])
  ): Future[Either[ErrorInfo, Seq[Book]]] = {
    val qSeq: Seq[(String, Option[String])] =
      Seq(("title", query._1), ("author", query._2), ("published", query._3), ("pages", query._4))
        .filterNot(_._2.isEmpty)

    find(qSeq)
  }

  def findByFieldRoute: Route = findByFieldEndpoint.toRoute(findByField)

  // Обновляем сущность
  def updateEndpoint(): Endpoint[Book, ErrorInfo, Book, Any] =
    baseEndpoint.put
      .description("Обновляем книгу")
      .in(jsonBody[Book])
      .out(jsonBody[Book])

  def updateRoute(): Route = updateEndpoint().toRoute(update)

  // Удаляем сущность по Id
  def deleteEndpoint(): Endpoint[UUID, ErrorInfo, Book, Any] =
    baseEndpoint.delete
      .description("Удаляем книгу по Id")
      .in(path[UUID])
      .out(jsonBody[Book])

  def deleteRoute(): Route = deleteEndpoint().toRoute(delete)
}

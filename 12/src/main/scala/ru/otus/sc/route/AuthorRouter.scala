package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.concat
import ru.otus.sc.model.author._
import ru.otus.sc.service.AuthorService
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import io.circe.Encoder._
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import scala.concurrent.{ExecutionContextExecutor, Future}

class AuthorRouter(pathPrefix: String, service: AuthorService) extends BaseRouter {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def route: Route =
    concat(
      findAllAuthorsRoute,
      getAuthorRoute,
      findAuthorByLastNameRoute,
      createAuthorRoute,
      deleteAuthorRoute(),
      updateAuthorRoute()
    )

  def endpoints =
    List(
      findAllAuthorsEndpoint,
      getAuthorEndpoint,
      findAuthorByLastNameEndpoint,
      createAuthorEndpoint,
      deleteAuthorEndpoint(),
      updateAuthorEndpoint()
    )

  private val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.in(pathPrefix).in("author").errorOut(stringBody)

  // Выводим всех авторов
  private def findAllAuthorsEndpoint: Endpoint[Unit, String, Seq[Author], Any] =
    baseEndpoint.get
      .description("Вывод всех авторов")
      .out(jsonBody[Seq[Author]])

  private def findAllAuthors: Future[Either[String, Seq[Author]]] = {
    service.findAuthor(FindAuthorRequest.All()) map {
      case FindAuthorResponse.Result(authorSeq) => Right(authorSeq)
      case _                                    => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findAllAuthorsRoute: Route = findAllAuthorsEndpoint.toRoute(_ => findAllAuthors)

  // Выводим автора по id
  private def getAuthorEndpoint: Endpoint[UUID, String, Author, Any] =
    baseEndpoint.get
      .description("Вывод конкретного автора по Id")
      .in(path[UUID])
      .out(jsonBody[Author])

  private def getAuthor(id: UUID): Future[Either[String, Author]] = {
    service.getAuthor(GetAuthorRequest(id)) map {
      case GetAuthorResponse.Found(role) => Right(role)
      case GetAuthorResponse.NotFound(_) => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def getAuthorRoute: Route = getAuthorEndpoint.toRoute(getAuthor)

  // Ищем и выводим авторов по фамилии
  private def findAuthorByLastNameEndpoint: Endpoint[String, String, Seq[Author], Any] =
    baseEndpoint.get
      .description("Поиск авторов по фамилии")
      .in("find")
      .in(query[String]("lastname"))
      .out(jsonBody[Seq[Author]])

  private def findAuthor(lastname: String): Future[Either[String, Seq[Author]]] = {
    service.findAuthor(FindAuthorRequest.ByLastName(lastname)) map {
      case FindAuthorResponse.Result(role) => Right(role)
      case _                               => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findAuthorByLastNameRoute: Route = findAuthorByLastNameEndpoint.toRoute(findAuthor)

  // Создаём автора
  private def createAuthorEndpoint: Endpoint[Author, String, Author, Any] =
    baseEndpoint.post
      .description("Создаём автора")
      .in(jsonBody[Author])
      .out(jsonBody[Author])

  private def createAuthor(role: Author): Future[Either[String, Author]] =
    service.createAuthor(CreateAuthorRequest(role)) map {
      case CreateAuthorResponse(response) => Right(response)
      case _                              => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def createAuthorRoute: Route = createAuthorEndpoint.toRoute(createAuthor)

  // Удаляем автора по Id
  private def deleteAuthorEndpoint(): Endpoint[UUID, String, Author, Any] =
    baseEndpoint.delete
      .description("Удаляем автора по Id")
      .in(path[UUID])
      .out(jsonBody[Author])

  private def deleteAuthor(id: UUID): Future[Either[String, Author]] =
    service.deleteAuthor(DeleteAuthorRequest(id)) map {
      case DeleteAuthorResponse.Deleted(role) => Right(role)
      case DeleteAuthorResponse.NotFound(_)   => Left(StatusCodes.NotFound.defaultMessage)
    }

  private def deleteAuthorRoute(): Route = deleteAuthorEndpoint().toRoute(deleteAuthor)

  // Обновляем автора
  private def updateAuthorEndpoint(): Endpoint[Author, String, Author, Any] =
    baseEndpoint.put
      .description("Обновляем автора")
      .in(jsonBody[Author])
      .out(jsonBody[Author])

  private def updateAuthor(role: Author): Future[Either[String, Author]] =
    service.updateAuthor(UpdateAuthorRequest(role)) map {
      case UpdateAuthorResponse.Updated(role) => Right(role)
      case UpdateAuthorResponse.NotFound(_)   => Left(StatusCodes.NotFound.defaultMessage)
      case UpdateAuthorResponse.CantUpdateAuthorWithoutId =>
        Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def updateAuthorRoute(): Route = updateAuthorEndpoint().toRoute(updateAuthor)
}

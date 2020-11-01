package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.concat
import ru.otus.sc.model.book._
import ru.otus.sc.service.BookService
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import ru.otus.sc.model.author.Author
import sttp.model._
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import io.circe.Encoder._
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import scala.concurrent.{ExecutionContextExecutor, Future}

class BookRouter(pathPrefix: String, service: BookService) extends BaseRouter {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def route: Route =
    concat(
      findAllBooksRoute,
      getBookRoute,
      findBooksRoute,
      createBookRoute,
      deleteBookRoute(),
      updateBookRoute()
    )

  def endpoints =
    List(
      findAllBooksEndpoint,
      getBookEndpoint,
      findBooksEndpoint,
      createBookEndpoint,
      deleteBookEndpoint(),
      updateBookEndpoint()
    )

  private val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.in(pathPrefix).in("book").errorOut(stringBody)

  // Выводим все книги
  private def findAllBooksEndpoint: Endpoint[Unit, String, Seq[Book], Any] =
    baseEndpoint.get
      .description("Вывод всех книги")
      .out(jsonBody[Seq[Book]])

  private def findAllBooks: Future[Either[String, Seq[Book]]] = {
    service.findBook(FindBookRequest.All()) map {
      case FindBookResponse.Result(bookSeq) => Right(bookSeq)
      case _                                => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findAllBooksRoute: Route = findAllBooksEndpoint.toRoute(_ => findAllBooks)

  // Выводим книгу по id
  private def getBookEndpoint: Endpoint[UUID, String, Book, Any] =
    baseEndpoint.get
      .description("Вывод конкретной книги по Id")
      .in(path[UUID])
      .out(jsonBody[Book])

  private def getBook(id: UUID): Future[Either[String, Book]] = {
    service.getBook(GetBookRequest(id)) map {
      case GetBookResponse.Found(book) => Right(book)
      case GetBookResponse.NotFound(_) => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def getBookRoute: Route = getBookEndpoint.toRoute(getBook)

  // Ищем книги по параметрам
  private def findBooksEndpoint: Endpoint[QueryParams, String, Seq[Book], Any] =
    baseEndpoint.get
      .description(
        "Поиск книг по параметрам: " +
          "'title' - название, " +
          "'authorId' - Id автора, " +
          "'year' - год издания, " +
          "'pages' - кол-во страниц"
      )
      .in("find")
      .in(queryParams)
      .out(jsonBody[Seq[Book]])

  private def findBooks(queryParams: QueryParams): Future[Either[String, Seq[Book]]] = {
    val queryParamsMap: Map[String, String] = queryParams.toMap
    val queryParamsKeySet: Set[String]      = queryParamsMap.keySet

    if (queryParamsKeySet.contains("title"))
      service.findBook(FindBookRequest.ByTitle(queryParamsMap.getOrElse("title", ""))) map {
        case FindBookResponse.Result(book) => Right(book)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("authorId"))
      service.findBook(
        FindBookRequest.ByAuthor(
          new Author(Some(UUID.fromString(queryParamsMap.getOrElse("authorId", ""))), "", "")
        )
      ) map {
        case FindBookResponse.Result(book) => Right(book)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("year"))
      service.findBook(FindBookRequest.ByYear(queryParamsMap.getOrElse("year", "").toInt)) map {
        case FindBookResponse.Result(book) => Right(book)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("pages"))
      service.findBook(FindBookRequest.ByPages(queryParamsMap.getOrElse("pages", "").toInt)) map {
        case FindBookResponse.Result(book) => Right(book)
        case _                             => Left(StatusCodes.NotFound.defaultMessage)
      }
    else Future(Left(StatusCodes.BadRequest.defaultMessage))
  }

  private def findBooksRoute: Route = findBooksEndpoint.toRoute(findBooks)

  // Создаём книгу
  private def createBookEndpoint: Endpoint[Book, String, Book, Any] =
    baseEndpoint.post
      .description("Создаём книгу")
      .in(jsonBody[Book])
      .out(jsonBody[Book])

  private def createBook(book: Book): Future[Either[String, Book]] =
    service.createBook(CreateBookRequest(book)) map {
      case CreateBookResponse(response) => Right(response)
      case _                            => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def createBookRoute: Route = createBookEndpoint.toRoute(createBook)

  // Удаляем книгу по Id
  private def deleteBookEndpoint(): Endpoint[UUID, String, Book, Any] =
    baseEndpoint.delete
      .description("Удаляем книгу по Id")
      .in(path[UUID])
      .out(jsonBody[Book])

  private def deleteBook(id: UUID): Future[Either[String, Book]] =
    service.deleteBook(DeleteBookRequest(id)) map {
      case DeleteBookResponse.Deleted(book) => Right(book)
      case DeleteBookResponse.NotFound(_)   => Left(StatusCodes.NotFound.defaultMessage)
    }

  private def deleteBookRoute(): Route = deleteBookEndpoint().toRoute(deleteBook)

  // Обновляем книгу
  private def updateBookEndpoint(): Endpoint[Book, String, Book, Any] =
    baseEndpoint.put
      .description("Обновляем книгу")
      .in(jsonBody[Book])
      .out(jsonBody[Book])

  private def updateBook(book: Book): Future[Either[String, Book]] =
    service.updateBook(UpdateBookRequest(book)) map {
      case UpdateBookResponse.Updated(book)           => Right(book)
      case UpdateBookResponse.NotFound(_)             => Left(StatusCodes.NotFound.defaultMessage)
      case UpdateBookResponse.CantUpdateBookWithoutId => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def updateBookRoute(): Route = updateBookEndpoint().toRoute(updateBook)
}

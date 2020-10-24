package ru.otus.sc.route

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route
import sttp.tapir._
import sttp.model._
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import ru.otus.sc.model.record._
import ru.otus.sc.service.RecordService
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.user.User
import io.circe.Encoder._
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import sttp.tapir.json.circe.jsonBody
import scala.concurrent.{ExecutionContextExecutor, Future}

class RecordRouter(pathPrefix: String, service: RecordService) extends BaseRouter {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def route: Route =
    concat(
      findAllRecordsRoute,
      getRecordRoute,
      findRecordsRoute,
      createRecordRoute,
      deleteRecordRoute(),
      updateRecordRoute()
    )

  def endpoints =
    List(
      findAllRecordsEndpoint,
      getRecordEndpoint,
      findRecordsEndpoint,
      createRecordEndpoint,
      deleteRecordEndpoint(),
      updateRecordEndpoint()
    )

  private val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.in(pathPrefix).in("record").errorOut(stringBody)

  // Выводим все записи
  private def findAllRecordsEndpoint: Endpoint[Unit, String, Seq[Record], Any] =
    baseEndpoint.get
      .description("Вывод всех записи")
      .out(jsonBody[Seq[Record]])

  private def findAllRecords: Future[Either[String, Seq[Record]]] = {
    service.findRecord(FindRecordRequest.All()) map {
      case FindRecordResponse.Result(recordSeq) => Right(recordSeq)
      case _                                    => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def findAllRecordsRoute: Route = findAllRecordsEndpoint.toRoute(_ => findAllRecords)

  // Выводим запись по id
  private def getRecordEndpoint: Endpoint[UUID, String, Record, Any] =
    baseEndpoint.get
      .description("Вывод конкретной записи по Id")
      .in(path[UUID])
      .out(jsonBody[Record])

  private def getRecord(id: UUID): Future[Either[String, Record]] = {
    service.getRecord(GetRecordRequest(id)) map {
      case GetRecordResponse.Found(record) => Right(record)
      case GetRecordResponse.NotFound(_)   => Left(StatusCodes.NotFound.defaultMessage)
    }
  }

  private def getRecordRoute: Route = getRecordEndpoint.toRoute(getRecord)

  // Ищем записи по параметрам
  private val format: DateTimeFormatter            = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
  private def str2time(str: String): LocalDateTime = LocalDateTime.parse(str, format)

  private def findRecordsEndpoint: Endpoint[QueryParams, String, Seq[Record], Any] =
    baseEndpoint.get
      .description(
        "Поиск книг по параметрам: " +
          "'userId' - Id пользователя, " +
          "'bookId' - Id книги, " +
          "'getDT' - yyyyMMddTHHmmss когда книгу взяли, " +
          "'returnDT' - yyyyMMddTHHmmss когда книгу вернули"
      )
      .in("find")
      .in(queryParams)
      .out(jsonBody[Seq[Record]])

  private def findRecords(queryParams: QueryParams): Future[Either[String, Seq[Record]]] = {
    val queryParamsMap: Map[String, String] = queryParams.toMap
    val queryParamsKeySet: Set[String]      = queryParamsMap.keySet

    if (queryParamsKeySet.contains("userId"))
      service.findRecord(
        FindRecordRequest.ByUser(
          new User(
            Some(UUID.fromString(queryParamsMap.getOrElse("userId", ""))),
            "",
            "",
            "",
            "",
            0,
            Set()
          )
        )
      ) map {
        case FindRecordResponse.Result(record) => Right(record)
        case _                                 => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("bookId"))
      service.findRecord(
        FindRecordRequest.ByBook(
          new Book(Some(UUID.fromString(queryParamsMap.getOrElse("bookId", ""))), "", Set(), 0, 0)
        )
      ) map {
        case FindRecordResponse.Result(record) => Right(record)
        case _                                 => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("getDT"))
      service.findRecord(
        FindRecordRequest.ByGet(str2time(queryParamsMap.getOrElse("getDT", "")))
      ) map {
        case FindRecordResponse.Result(record) => Right(record)
        case _                                 => Left(StatusCodes.NotFound.defaultMessage)
      }
    else if (queryParamsKeySet.contains("returnDT"))
      service.findRecord(
        FindRecordRequest.ByReturn(str2time(queryParamsMap.getOrElse("returnDT", "")))
      ) map {
        case FindRecordResponse.Result(record) => Right(record)
        case _                                 => Left(StatusCodes.NotFound.defaultMessage)
      }
    else Future(Left(StatusCodes.BadRequest.defaultMessage))
  }

  private def findRecordsRoute: Route = findRecordsEndpoint.toRoute(findRecords)

  // Создаём запись
  private def createRecordEndpoint: Endpoint[Record, String, Record, Any] =
    baseEndpoint.post
      .description("Создаём запись")
      .in(jsonBody[Record])
      .out(jsonBody[Record])

  private def createRecord(record: Record): Future[Either[String, Record]] =
    service.createRecord(CreateRecordRequest(record)) map {
      case CreateRecordResponse(response) => Right(response)
      case _                              => Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def createRecordRoute: Route = createRecordEndpoint.toRoute(createRecord)

  // Удаляем запись по Id
  private def deleteRecordEndpoint(): Endpoint[UUID, String, Record, Any] =
    baseEndpoint.delete
      .description("Удаляем запись по Id")
      .in(path[UUID])
      .out(jsonBody[Record])

  private def deleteRecord(id: UUID): Future[Either[String, Record]] =
    service.deleteRecord(DeleteRecordRequest(id)) map {
      case DeleteRecordResponse.Deleted(record) => Right(record)
      case DeleteRecordResponse.NotFound(_)     => Left(StatusCodes.NotFound.defaultMessage)
    }

  private def deleteRecordRoute(): Route = deleteRecordEndpoint().toRoute(deleteRecord)

  // Обновляем запись
  private def updateRecordEndpoint(): Endpoint[Record, String, Record, Any] =
    baseEndpoint.put
      .description("Обновляем запись")
      .in(jsonBody[Record])
      .out(jsonBody[Record])

  private def updateRecord(record: Record): Future[Either[String, Record]] =
    service.updateRecord(UpdateRecordRequest(record)) map {
      case UpdateRecordResponse.Updated(record) => Right(record)
      case UpdateRecordResponse.NotFound(_)     => Left(StatusCodes.NotFound.defaultMessage)
      case UpdateRecordResponse.CantUpdateRecordWithoutId =>
        Left(StatusCodes.BadRequest.defaultMessage)
    }

  private def updateRecordRoute(): Route = updateRecordEndpoint().toRoute(updateRecord)
}

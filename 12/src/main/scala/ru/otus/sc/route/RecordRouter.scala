package ru.otus.sc.route

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.model.record._
import ru.otus.sc.service.RecordService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.json.RecordJsonProtocol._
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.user.User

class RecordRouter(service: RecordService) extends BaseRouter {
  def route: Route =
    pathPrefix("record") {
      getRecord ~
        findRecordByUserId ~
        findRecordByBookId ~
        findRecordByGet ~
        findRecordByReturn ~
        findAllRecords ~
        createRecord ~
        updateRecord ~
        deleteRecord
    }

  private def getRecord: Route = {
    (get & parameter("id".as[UUID])) { uuid =>
      onSuccess(service.getRecord(GetRecordRequest(uuid))) {
        case GetRecordResponse.Found(record) => complete(record)
        case GetRecordResponse.NotFound(_)   => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findRecordByUserId: Route = {
    (get & parameter("userId".as[UUID])) { userId =>
      onSuccess(
        service.findRecord(
          FindRecordRequest.ByUser(new User(Some(userId), "", "", "", "", 0, Set()))
        )
      ) {
        case FindRecordResponse.Result(recordSeq) => complete(recordSeq)
        case _                                    => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findRecordByBookId: Route = {
    (get & parameter("bookId".as[UUID])) { bookId =>
      onSuccess(
        service.findRecord(
          FindRecordRequest.ByBook(new Book(Some(bookId), "", Set(), 0, 0))
        )
      ) {
        case FindRecordResponse.Result(recordSeq) => complete(recordSeq)
        case _                                    => complete(StatusCodes.NotFound)
      }
    }
  }

  private val format                = new SimpleDateFormat("yyyyMMdd'T'HHmmss")
  private def str2time(str: String) = new Timestamp(format.parse(str).getTime)

  private def findRecordByGet: Route = {
    (get & parameter("getDT".as[String])) { dt =>
      try {
        onSuccess(service.findRecord(FindRecordRequest.ByGet(str2time(dt)))) {
          case FindRecordResponse.Result(recordSeq) => complete(recordSeq)
          case _                                    => complete(StatusCodes.NotFound)
        }
      } catch {
        case _: Throwable => complete(StatusCodes.BadRequest)
      }
    }
  }

  private def findRecordByReturn: Route = {
    (get & parameter("returnDT".as[String])) { dt =>
      try {
        onSuccess(service.findRecord(FindRecordRequest.ByReturn(str2time(dt)))) {
          case FindRecordResponse.Result(recordSeq) => complete(recordSeq)
          case _                                    => complete(StatusCodes.NotFound)
        }
      } catch {
        case _: Throwable => complete(StatusCodes.BadRequest)
      }
    }
  }

  private def findAllRecords: Route =
    get {
      onSuccess(service.findRecord(FindRecordRequest.All())) {
        case FindRecordResponse.Result(recordSeq) => complete(recordSeq)
        case _                                    => complete(StatusCodes.NotFound)
      }
    }

  private def createRecord: Route =
    (post & entity(as[Record])) { record =>
      onSuccess(service.createRecord(CreateRecordRequest(record))) { response =>
        complete(response.record)
      }
    }

  private def updateRecord: Route =
    (put & entity(as[Record])) { record =>
      onSuccess(service.updateRecord(UpdateRecordRequest(record))) {
        case UpdateRecordResponse.Updated(record)           => complete(record)
        case UpdateRecordResponse.NotFound(_)               => complete(StatusCodes.NotFound)
        case UpdateRecordResponse.CantUpdateRecordWithoutId => complete(StatusCodes.BadRequest)
      }
    }

  private def deleteRecord: Route =
    (delete & path(JavaUUID.map(DeleteRecordRequest))) { recordIdRequest =>
      onSuccess(service.deleteRecord(recordIdRequest)) {
        case DeleteRecordResponse.Deleted(record) => complete(record)
        case DeleteRecordResponse.NotFound(_)     => complete(StatusCodes.NotFound)
      }
    }
}

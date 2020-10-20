package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.model.author._
import ru.otus.sc.service.AuthorService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.json.AuthorJsonProtocol._

class AuthorRouter(service: AuthorService) extends BaseRouter {
  def route: Route =
    pathPrefix("author") {
      getAuthor ~
        findAuthorByLastName ~
        findAllAuthors ~
        createAuthor ~
        updateAuthor ~
        deleteAuthor
    }

  private def getAuthor: Route = {
    (get & parameter("id".as[UUID])) { uuid =>
      onSuccess(service.getAuthor(GetAuthorRequest(uuid))) {
        case GetAuthorResponse.Found(author) => complete(author)
        case GetAuthorResponse.NotFound(_)   => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findAuthorByLastName: Route = {
    (get & parameter("lastname".as[String])) { lastName =>
      onSuccess(service.findAuthor(FindAuthorRequest.ByLastName(lastName))) {
        case FindAuthorResponse.Result(authorSeq) => complete(authorSeq)
        case _                                    => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findAllAuthors: Route =
    get {
      onSuccess(service.findAuthor(FindAuthorRequest.All())) {
        case FindAuthorResponse.Result(authorSeq) => complete(authorSeq)
        case _                                    => complete(StatusCodes.NotFound)
      }
    }

  private def createAuthor: Route =
    (post & entity(as[Author])) { author =>
      onSuccess(service.createAuthor(CreateAuthorRequest(author))) { response =>
        complete(response.author)
      }
    }

  private def updateAuthor: Route =
    (put & entity(as[Author])) { author =>
      onSuccess(service.updateAuthor(UpdateAuthorRequest(author))) {
        case UpdateAuthorResponse.Updated(author)           => complete(author)
        case UpdateAuthorResponse.NotFound(_)               => complete(StatusCodes.NotFound)
        case UpdateAuthorResponse.CantUpdateAuthorWithoutId => complete(StatusCodes.BadRequest)
      }
    }

  private def deleteAuthor: Route =
    (delete & path(JavaUUID.map(DeleteAuthorRequest))) { authorIdRequest =>
      onSuccess(service.deleteAuthor(authorIdRequest)) {
        case DeleteAuthorResponse.Deleted(author) => complete(author)
        case DeleteAuthorResponse.NotFound(_)     => complete(StatusCodes.NotFound)
      }
    }
}

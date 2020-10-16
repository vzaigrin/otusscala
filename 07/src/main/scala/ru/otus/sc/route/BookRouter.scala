package ru.otus.sc.route

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.model.book._
import ru.otus.sc.service.BookService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.json.BookJsonProtocol._
import ru.otus.sc.model.author.Author

class BookRouter(service: BookService) extends BaseRouter {
  def route: Route =
    pathPrefix("book") {
      getBook ~
        findBookByTitle ~
        findBookByAuthorId ~
        findBookByYear ~
        findBookByPages ~
        findAllBooks ~
        createBook ~
        updateBook ~
        deleteBook
    }

  private def getBook: Route = {
    (get & parameter("id".as[UUID])) { uuid =>
      onSuccess(service.getBook(GetBookRequest(uuid))) {
        case GetBookResponse.Found(book) => complete(book)
        case GetBookResponse.NotFound(_) => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findBookByTitle: Route = {
    (get & parameter("title".as[String])) { title =>
      onSuccess(service.findBook(FindBookRequest.ByTitle(title))) {
        case FindBookResponse.Result(bookSeq) => complete(bookSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findBookByAuthorId: Route = {
    (get & parameter("authorId".as[UUID])) { authorId =>
      onSuccess(service.findBook(FindBookRequest.ByAuthor(new Author(Some(authorId), "", "")))) {
        case FindBookResponse.Result(bookSeq) => complete(bookSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findBookByYear: Route = {
    (get & parameter("year".as[Int])) { year =>
      onSuccess(service.findBook(FindBookRequest.ByYear(year))) {
        case FindBookResponse.Result(bookSeq) => complete(bookSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findBookByPages: Route = {
    (get & parameter("pages".as[Int])) { pages =>
      onSuccess(service.findBook(FindBookRequest.ByPages(pages))) {
        case FindBookResponse.Result(bookSeq) => complete(bookSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }
  }

  private def findAllBooks: Route =
    get {
      onSuccess(service.findBook(FindBookRequest.All())) {
        case FindBookResponse.Result(bookSeq) => complete(bookSeq)
        case _                                => complete(StatusCodes.NotFound)
      }
    }

  private def createBook: Route =
    (post & entity(as[Book])) { book =>
      onSuccess(service.createBook(CreateBookRequest(book))) { response =>
        complete(response.book)
      }
    }

  private def updateBook: Route =
    (put & entity(as[Book])) { book =>
      onSuccess(service.updateBook(UpdateBookRequest(book))) {
        case UpdateBookResponse.Updated(book)           => complete(book)
        case UpdateBookResponse.NotFound(_)             => complete(StatusCodes.NotFound)
        case UpdateBookResponse.CantUpdateBookWithoutId => complete(StatusCodes.BadRequest)
      }
    }

  private def deleteBook: Route =
    (delete & path(JavaUUID.map(DeleteBookRequest))) { bookIdRequest =>
      onSuccess(service.deleteBook(bookIdRequest)) {
        case DeleteBookResponse.Deleted(book) => complete(book)
        case DeleteBookResponse.NotFound(_)   => complete(StatusCodes.NotFound)
      }
    }
}

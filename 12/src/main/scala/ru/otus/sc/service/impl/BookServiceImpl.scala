package ru.otus.sc.service.impl

import ru.otus.sc.dao.BookDao
import ru.otus.sc.model.book._
import ru.otus.sc.service.BookService
import scala.concurrent.{ExecutionContext, Future}

class BookServiceImpl(dao: BookDao)(implicit ec: ExecutionContext) extends BookService {
  override def createBook(request: CreateBookRequest): Future[CreateBookResponse] =
    dao.createBook(request.book).map(CreateBookResponse)

  override def getBook(request: GetBookRequest): Future[GetBookResponse] =
    dao.getBook(request.id) map {
      case Some(book) => GetBookResponse.Found(book)
      case None       => GetBookResponse.NotFound(request.id)
    }

  override def updateBook(request: UpdateBookRequest): Future[UpdateBookResponse] =
    request.book.id match {
      case None => Future.successful(UpdateBookResponse.CantUpdateBookWithoutId)
      case Some(bookId) =>
        dao.updateBook(request.book) map {
          case Some(book) => UpdateBookResponse.Updated(book)
          case None       => UpdateBookResponse.NotFound(bookId)
        }
    }

  override def deleteBook(request: DeleteBookRequest): Future[DeleteBookResponse] =
    dao
      .deleteBook(request.id)
      .map {
        _.map(DeleteBookResponse.Deleted)
          .getOrElse(DeleteBookResponse.NotFound(request.id))
      }

  override def findBook(request: FindBookRequest): Future[FindBookResponse] =
    request match {
      case FindBookRequest.ByTitle(title)   => dao.findByTitle(title).map(FindBookResponse.Result)
      case FindBookRequest.ByAuthor(author) => dao.findByAuthor(author).map(FindBookResponse.Result)
      case FindBookRequest.ByYear(year)     => dao.findByYear(year).map(FindBookResponse.Result)
      case FindBookRequest.ByPages(pages)   => dao.findByPages(pages).map(FindBookResponse.Result)
      case FindBookRequest.All()            => dao.findAll().map(FindBookResponse.Result)
    }

}

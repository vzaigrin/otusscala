package ru.otus.sc.book.service.impl

import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model._
import ru.otus.sc.book.service.BookService

class BookServiceImpl(dao: BookDao) extends BookService {
  override def createBook(request: CreateBookRequest): CreateBookResponse =
    CreateBookResponse(dao.createBook(request.book))

  override def getBook(request: GetBookRequest): GetBookResponse =
    dao.getBook(request.id) match {
      case Some(book) => GetBookResponse.Found(book)
      case None       => GetBookResponse.NotFound(request.id)
    }

  override def updateBook(request: UpdateBookRequest): UpdateBookResponse =
    request.book.id match {
      case None => UpdateBookResponse.CantUpdateBookWithoutId
      case Some(bookId) =>
        dao.updateBook(request.book) match {
          case Some(book) => UpdateBookResponse.Updated(book)
          case None       => UpdateBookResponse.NotFound(bookId)
        }
    }

  override def deleteBook(request: DeleteBookRequest): DeleteBookResponse =
    dao
      .deleteBook(request.id)
      .map(DeleteBookResponse.Deleted)
      .getOrElse(DeleteBookResponse.NotFound(request.id))

  override def findBooks(request: FindBooksRequest): FindBooksResponse =
    request match {
      case FindBooksRequest.ByTitle(title) =>
        FindBooksResponse.Result(dao.findByTitle(title))
      case FindBooksRequest.ByAuthor(author) =>
        FindBooksResponse.Result(dao.findByAuthor(author))
      case FindBooksRequest.ByPages(pages) =>
        FindBooksResponse.Result(dao.findByPages(pages))
      case FindBooksRequest.ByYear(year) =>
        FindBooksResponse.Result(dao.findByYear(year))
      case FindBooksRequest.All() =>
        FindBooksResponse.Result(dao.findAll())
    }

}

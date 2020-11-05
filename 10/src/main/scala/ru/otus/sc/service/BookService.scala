package ru.otus.sc.service

import ru.otus.sc.model.book._
import scala.concurrent.Future

trait BookService {
  def createBook(request: CreateBookRequest): Future[CreateBookResponse]
  def getBook(request: GetBookRequest): Future[GetBookResponse]
  def updateBook(request: UpdateBookRequest): Future[UpdateBookResponse]
  def deleteBook(request: DeleteBookRequest): Future[DeleteBookResponse]
  def findBook(request: FindBookRequest): Future[FindBookResponse]
}

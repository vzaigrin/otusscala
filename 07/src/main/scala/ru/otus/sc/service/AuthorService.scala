package ru.otus.sc.service

import ru.otus.sc.model.author._
import scala.concurrent.Future

trait AuthorService {
  def createAuthor(request: CreateAuthorRequest): Future[CreateAuthorResponse]
  def getAuthor(request: GetAuthorRequest): Future[GetAuthorResponse]
  def updateAuthor(request: UpdateAuthorRequest): Future[UpdateAuthorResponse]
  def deleteAuthor(request: DeleteAuthorRequest): Future[DeleteAuthorResponse]
  def findAuthor(request: FindAuthorRequest): Future[FindAuthorResponse]
}

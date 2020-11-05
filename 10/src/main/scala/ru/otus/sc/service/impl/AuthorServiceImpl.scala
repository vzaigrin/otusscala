package ru.otus.sc.service.impl

import ru.otus.sc.dao.AuthorDao
import ru.otus.sc.model.author._
import ru.otus.sc.service.AuthorService
import scala.concurrent.{ExecutionContext, Future}

class AuthorServiceImpl(dao: AuthorDao)(implicit ec: ExecutionContext) extends AuthorService {
  override def createAuthor(request: CreateAuthorRequest): Future[CreateAuthorResponse] =
    dao.createAuthor(request.author).map(CreateAuthorResponse)

  override def getAuthor(request: GetAuthorRequest): Future[GetAuthorResponse] =
    dao.getAuthor(request.id) map {
      case Some(author) => GetAuthorResponse.Found(author)
      case None         => GetAuthorResponse.NotFound(request.id)
    }

  override def updateAuthor(request: UpdateAuthorRequest): Future[UpdateAuthorResponse] =
    request.author.id match {
      case None => Future.successful(UpdateAuthorResponse.CantUpdateAuthorWithoutId)
      case Some(authorId) =>
        dao.updateAuthor(request.author) map {
          case Some(author) => UpdateAuthorResponse.Updated(author)
          case None         => UpdateAuthorResponse.NotFound(authorId)
        }
    }

  override def deleteAuthor(request: DeleteAuthorRequest): Future[DeleteAuthorResponse] =
    dao
      .deleteAuthor(request.id)
      .map {
        _.map(DeleteAuthorResponse.Deleted)
          .getOrElse(DeleteAuthorResponse.NotFound(request.id))
      }

  override def findAuthor(request: FindAuthorRequest): Future[FindAuthorResponse] =
    request match {
      case FindAuthorRequest.ByLastName(lastName) =>
        dao.findByLastName(lastName).map(FindAuthorResponse.Result)
      case FindAuthorRequest.All() => dao.findAll().map(FindAuthorResponse.Result)
    }

}

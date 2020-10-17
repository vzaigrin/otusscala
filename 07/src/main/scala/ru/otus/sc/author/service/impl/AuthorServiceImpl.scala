package ru.otus.sc.author.service.impl

import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model._
import ru.otus.sc.author.service.AuthorService

class AuthorServiceImpl(dao: AuthorDao) extends AuthorService {
  override def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse =
    CreateAuthorResponse(dao.createAuthor(request.author))

  override def getAuthor(request: GetAuthorRequest): GetAuthorResponse =
    dao.getAuthor(request.id) match {
      case Some(author) => GetAuthorResponse.Found(author)
      case None         => GetAuthorResponse.NotFound(request.id)
    }

  override def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse =
    request.author.id match {
      case None => UpdateAuthorResponse.CantUpdateAuthorWithoutId
      case Some(authorId) =>
        dao.updateAuthor(request.author) match {
          case Some(author) => UpdateAuthorResponse.Updated(author)
          case None         => UpdateAuthorResponse.NotFound(authorId)
        }
    }

  override def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse =
    dao
      .deleteAuthor(request.id)
      .map(DeleteAuthorResponse.Deleted)
      .getOrElse(DeleteAuthorResponse.NotFound(request.id))

  override def findAuthors(request: FindAuthorsRequest): FindAuthorsResponse =
    request match {
      case FindAuthorsRequest.ByLastName(lastName) =>
        FindAuthorsResponse.Result(dao.findByLastName(lastName))
      case FindAuthorsRequest.All() =>
        FindAuthorsResponse.Result(dao.findAll())
    }

}

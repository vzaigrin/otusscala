package ru.otus.sc.author.service

import ru.otus.sc.author.model._

trait AuthorService {
  def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse
  def getAuthor(request: GetAuthorRequest): GetAuthorResponse
  def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse
  def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse
  def findAuthors(request: FindAuthorsRequest): FindAuthorsResponse
}

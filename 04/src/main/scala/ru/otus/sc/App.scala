package ru.otus.sc

import ru.otus.sc.user.dao.map.UserDaoMapImpl
import ru.otus.sc.user.model._
import ru.otus.sc.user.service.UserService
import ru.otus.sc.user.service.impl.UserServiceImpl
import ru.otus.sc.author.dao.impl.AuthorDaoImpl
import ru.otus.sc.author.model._
import ru.otus.sc.author.service._
import ru.otus.sc.author.service.impl._
import ru.otus.sc.book.dao.impl.BookDaoImpl
import ru.otus.sc.book.model._
import ru.otus.sc.book.service.BookService
import ru.otus.sc.book.service.impl.BookServiceImpl

trait App {
  def createUser(request: CreateUserRequest): CreateUserResponse
  def getUser(request: GetUserRequest): GetUserResponse
  def updateUser(request: UpdateUserRequest): UpdateUserResponse
  def deleteUser(request: DeleteUserRequest): DeleteUserResponse
  def findUsers(request: FindUsersRequest): FindUsersResponse
  def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse
  def getAuthor(request: GetAuthorRequest): GetAuthorResponse
  def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse
  def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse
  def findAuthors(request: FindAuthorsRequest): FindAuthorsResponse
  def createBook(request: CreateBookRequest): CreateBookResponse
  def getBook(request: GetBookRequest): GetBookResponse
  def updateBook(request: UpdateBookRequest): UpdateBookResponse
  def deleteBook(request: DeleteBookRequest): DeleteBookResponse
  def findBooks(request: FindBooksRequest): FindBooksResponse
}

object App {
  private class AppImpl(
      userService: UserService,
      authorService: AuthorService,
      bookService: BookService
  ) extends App {
    def createUser(request: CreateUserRequest): CreateUserResponse = userService.createUser(request)
    def getUser(request: GetUserRequest): GetUserResponse          = userService.getUser(request)
    def updateUser(request: UpdateUserRequest): UpdateUserResponse = userService.updateUser(request)
    def deleteUser(request: DeleteUserRequest): DeleteUserResponse = userService.deleteUser(request)
    def findUsers(request: FindUsersRequest): FindUsersResponse    = userService.findUsers(request)

    def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse =
      authorService.createAuthor(request)
    def getAuthor(request: GetAuthorRequest): GetAuthorResponse = authorService.getAuthor(request)
    def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse =
      authorService.updateAuthor(request)
    def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse =
      authorService.deleteAuthor(request)
    def findAuthors(request: FindAuthorsRequest): FindAuthorsResponse =
      authorService.findAuthors(request)

    def createBook(request: CreateBookRequest): CreateBookResponse = bookService.createBook(request)
    def getBook(request: GetBookRequest): GetBookResponse          = bookService.getBook(request)
    def updateBook(request: UpdateBookRequest): UpdateBookResponse = bookService.updateBook(request)
    def deleteBook(request: DeleteBookRequest): DeleteBookResponse = bookService.deleteBook(request)
    def findBooks(request: FindBooksRequest): FindBooksResponse    = bookService.findBooks(request)
  }

  def apply(): App = {
    val userDao       = new UserDaoMapImpl
    val userService   = new UserServiceImpl(userDao)
    val authorDao     = new AuthorDaoImpl
    val authorService = new AuthorServiceImpl(authorDao)
    val bookDao       = new BookDaoImpl
    val bookService   = new BookServiceImpl(bookDao)

    new AppImpl(userService, authorService, bookService)
  }
}

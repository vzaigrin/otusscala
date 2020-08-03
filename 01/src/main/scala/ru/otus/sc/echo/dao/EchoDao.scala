package ru.otus.sc.echo.dao

// Трейт DAO для сервиса Echo
trait EchoDao {
  def echoPrefix: String
  def echoPostfix: String
}

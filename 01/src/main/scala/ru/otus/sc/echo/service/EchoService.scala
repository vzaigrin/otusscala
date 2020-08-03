package ru.otus.sc.echo.service

import ru.otus.sc.echo.model.{EchoRequest, EchoResponse}

// Сервис Echo
trait EchoService {
  def echo(request: EchoRequest): EchoResponse
}

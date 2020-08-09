package ru.otus.sc.echo.service.impl

import ru.otus.sc.echo.dao.EchoDao
import ru.otus.sc.echo.model.{EchoRequest, EchoResponse}
import ru.otus.sc.echo.service.EchoService

// Имплементация сервиса Echo
class EchoServiceImpl(dao: EchoDao) extends EchoService {
  override def echo(request: EchoRequest): EchoResponse =
    EchoResponse(request.request)
}

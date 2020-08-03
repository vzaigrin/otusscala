package ru.otus.sc.reverse.service.impl

import ru.otus.sc.reverse.dao.ReverseDao
import ru.otus.sc.reverse.model.{ReverseRequest, ReverseResponse}
import ru.otus.sc.reverse.service.ReverseService

// Имплементация сервиса Reverse
class ReverseServiceImpl(dao: ReverseDao) extends ReverseService {
  override def reverse(request: ReverseRequest): ReverseResponse =
    ReverseResponse(s"${dao.reversePrefix}${request.request.reverse}${dao.reversePostfix}")
}

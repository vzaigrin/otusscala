package ru.otus.sc.count.service.impl

import ru.otus.sc.count.dao.CountDao
import ru.otus.sc.count.model.{CountRequest, CountResponse}
import ru.otus.sc.count.service.CountService

// Имплементацмя сервиса Count
class CountServiceImpl(dao: CountDao) extends CountService {
  override def count(request: CountRequest): CountResponse = CountResponse(dao.count)
}

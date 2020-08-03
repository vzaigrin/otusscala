package ru.otus.sc.count.service

import ru.otus.sc.count.model.{CountRequest, CountResponse}

// Сервис Count
trait CountService {
  def count(request: CountRequest): CountResponse
}

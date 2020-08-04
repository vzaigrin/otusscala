package ru.otus.sc.reverse.service

import ru.otus.sc.reverse.model.{ReverseRequest, ReverseResponse}

// Трейт сервиса Reverse
trait ReverseService {
  def reverse(request: ReverseRequest): ReverseResponse
}

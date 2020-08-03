package ru.otus.sc.fib.service.impl

import ru.otus.sc.fib.dao.FibDao
import ru.otus.sc.fib.model.{FibRequest, FibResponse}
import ru.otus.sc.fib.service.FibService

// Имплеменетация сервиса Fibonacci
class FibServiceImpl(dao: FibDao) extends FibService {
  override def fib(input: FibRequest): FibResponse = FibResponse(dao.fib(input.input))
}

package ru.otus.sc.fib.service

import ru.otus.sc.fib.model.{FibRequest, FibResponse}

// Сервис Fibonacci
trait FibService {
  def fib(input: FibRequest): FibResponse
}

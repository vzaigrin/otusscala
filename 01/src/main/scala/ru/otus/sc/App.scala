package ru.otus.sc

import ru.otus.sc.count.model.{CountRequest, CountResponse}
import ru.otus.sc.count.service.CountService
import ru.otus.sc.count.dao.impl.CountDaoImpl
import ru.otus.sc.count.service.impl.CountServiceImpl
import ru.otus.sc.echo.dao.impl.EchoDaoImpl
import ru.otus.sc.echo.model.{EchoRequest, EchoResponse}
import ru.otus.sc.echo.service.EchoService
import ru.otus.sc.echo.service.impl.EchoServiceImpl
import ru.otus.sc.fib.dao.impl.FibDaoImpl
import ru.otus.sc.fib.model.{FibRequest, FibResponse}
import ru.otus.sc.fib.service.FibService
import ru.otus.sc.fib.service.impl.FibServiceImpl
import ru.otus.sc.greet.dao.impl.GreetingDaoImpl
import ru.otus.sc.greet.model.{GreetRequest, GreetResponse}
import ru.otus.sc.greet.service.GreetingService
import ru.otus.sc.greet.service.impl.GreetingServiceImpl
import ru.otus.sc.key.dao.impl.KeyDaoImpl
import ru.otus.sc.key.model.{KeyRequest, KeyResponse}
import ru.otus.sc.key.service.KeyService
import ru.otus.sc.key.service.impl.KeyServiceImpl
import ru.otus.sc.reverse.dao.impl.ReverseDaoImpl
import ru.otus.sc.reverse.model.{ReverseRequest, ReverseResponse}
import ru.otus.sc.reverse.service.ReverseService
import ru.otus.sc.reverse.service.impl.ReverseServiceImpl

// трейт со всеми методами
trait App {
  def greet(request: GreetRequest): GreetResponse
  def count(request: CountRequest): CountResponse
  def echo(request: EchoRequest): EchoResponse
  def key(request: KeyRequest): KeyResponse
  def fib(request: FibRequest): FibResponse
  def reverse(request: ReverseRequest): ReverseResponse
}

//
object App {
  private class AppImpl(
      greeting: GreetingService,
      counting: CountService,
      echo: EchoService,
      key: KeyService,
      fib: FibService,
      reverse: ReverseService
  ) extends App {
    def greet(request: GreetRequest): GreetResponse       = greeting.greet(request)
    def count(request: CountRequest): CountResponse       = counting.count(request)
    def echo(request: EchoRequest): EchoResponse          = echo.echo(request)
    def key(request: KeyRequest): KeyResponse             = key.key(request)
    def fib(request: FibRequest): FibResponse             = fib.fib(request)
    def reverse(request: ReverseRequest): ReverseResponse = reverse.reverse(request)
  }

  def apply(): App = {
    val greetingDao     = new GreetingDaoImpl
    val greetingService = new GreetingServiceImpl(greetingDao)

    val countDao     = CountDaoImpl
    val countService = new CountServiceImpl(countDao)

    val echoDao     = new EchoDaoImpl
    val echoService = new EchoServiceImpl(echoDao)

    val keyDao     = KeyDaoImpl
    val keyService = new KeyServiceImpl(keyDao)

    val fibDao     = new FibDaoImpl()
    val fibService = new FibServiceImpl(fibDao)

    val reverseDao     = new ReverseDaoImpl()
    val reverseService = new ReverseServiceImpl(reverseDao)

    new AppImpl(greetingService, countService, echoService, keyService, fibService, reverseService)
  }
}

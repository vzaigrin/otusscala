package ru.otus.sc

import ru.otus.sc.count.model.CountRequest
import ru.otus.sc.echo.model.EchoRequest
import ru.otus.sc.fib.model.FibRequest
import ru.otus.sc.greet.model.GreetRequest
import ru.otus.sc.key.model.KeyRequest
import ru.otus.sc.reverse.model.ReverseRequest

// Демо-программа, вызывает все сервисы
object Main {
  def main(args: Array[String]): Unit = {
    val app: App = App()

    // Greeting из примера
    println("Greet")
    val greetRequests: List[GreetRequest] =
      List(GreetRequest("John Doe"), GreetRequest("John Doe", isHuman = false))
    greetRequests foreach { request => println(s"$request: ${app.greet(request)}") }

    // Счетчик вызовов
    println("\nCount")
    val request: CountRequest = CountRequest("a")
    (1 to 5) foreach { _ => println(s"$request: ${app.count(request)}") }

    // Echo
    println("\nEcho")
    val echoRequests: List[EchoRequest] =
      List(EchoRequest("one"), EchoRequest("two"), EchoRequest("three"))
    echoRequests foreach { request => println(s"$request: ${app.echo(request)}") }

    // Хранилище значений по заранее заданному списку ключей
    println("\nKey")
    (0 to 10) foreach { c => println(s"$request: ${app.key(KeyRequest(c))}") }

    // Получение лениво вычисляемого значения
    println("\nFibonacci sequence")
    (0 to 10) foreach { c => println(s"${c.toString}: ${app.fib(FibRequest(c))}") }

    // Метод, не входящий в примеры
    // Reverse - возвращает перевернутую строку
    println("\nReverse")
    val reverseRequests: List[ReverseRequest] =
      List(ReverseRequest("123"), ReverseRequest("456"), ReverseRequest("789"))
    reverseRequests foreach { request => println(s"$request: ${app.reverse(request)}") }
  }
}

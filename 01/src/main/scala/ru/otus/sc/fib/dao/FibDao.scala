package ru.otus.sc.fib.dao

// Трейт DAO сервиса Fib
trait FibDao {
  def fib(input: Int): Long
}

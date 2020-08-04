package ru.otus.sc.fib.dao.impl

import ru.otus.sc.fib.dao.FibDao

// Имплементация Dao сервиса Fib
class FibDaoImpl extends FibDao {
  private lazy val lazyFib: LazyList[Int] =
    0 #:: 1 #:: lazyFib.zip(lazyFib.tail).map { case (a, b) => a + b }
  override def fib(input: Int): Long = lazyFib(input)
}

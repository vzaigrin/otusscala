package module1

object functions {


  /**
   * Функции
   * Функции методы
   * Функции значения (лямбды, анонимные ф-ции)
   * SAM
   * Вложенные ф-ции
   * High order functions
   * Сurrying - Процесс преобразования ф-ции из нескольких аргументов в последовательность ф-ций из одного аргумента
   */



   def foo(x: Int, y: Int): Int = {
     x + y
   }

  val foo2: (Int, Int) => Int = (z, c) => z + c

  def bar(x: Int, f : (Int, Int) => Int): (Int, Int) => Int = f

   val l: List[(Int, Int) => Int] = List(foo)



  bar(1, foo2)

  foo(1, 2)
  foo2(1, 2)


  /**
   *  Задание 1. Написать ф-цию метод isEven, которая будет вычислять является ли число четным
   */


  def isEven(x: Int): Boolean = x % 2 == 0


  /**
   * Задание 2. Написать ф-цию метод isOdd, которая будет вычислять является ли число нечетным
   */

  def isOdd(x: Int): Boolean = x % 2 != 0


  /**
   * Задание 3. Написать ф-цию метод filterEven, которая получает на вход массив чисел и возвращает массив тех из них,
   * которые являются четными
   */

  def filterEven(arr: Array[Int]): Array[Int] = {
    arr.filter(item => isEven(item))
  }

  /**
   * Задание 3. Написать ф-цию метод filterOdd, которая получает на вход массив чисел и возвращает массив тех из них,
   * которые являются нечетными
   */


  /**
   * return statement
   *
   *
   * val two = (x: Int) => { return x; 2 }
   *
   *
   * def sumItUp: Int = {
   *    def one(x: Int): Int = { return x; 1 }
   *    val two = (x: Int) => { return x; 2 }
   *    1 + one(2) + two(3)
   * }
   */



}

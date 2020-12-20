package lectures.futures

object MonadOptionExample extends App {

  object Monad {
    def unit[T](x: T): Monad[T] = ??? // Some("1")
  }

  trait Monad[T] {
    def flatMap[U](f: T => Monad[U]): Monad[U]
  }

  /** Цепочки вычислений с помощью flatMap */

  def one: Option[Int] = Some(1)

  def two: Option[Int] = Some(2)

  def none: Option[Int] = None

  /*  println(one
      .flatMap(x => none)
      .flatMap(x => two.flatMap(y => Some(x + y)))
    ) //none*/


  /** Проверка монадических законов */

  def squareFunction(x: Int): Option[Int] = Some(x * x) // f

  def incrementFunction(x: Int): Option[Int] = Some(x + 1) //g

  /**
   * Left unit law:
   * (unit(x) flatMap f) == f(x)
   * */
  def leftUnitLaw(): Unit = {
    assert(Some(1).flatMap(squareFunction) == squareFunction(1))
    println("leftUnitLaw check success")
  }

  leftUnitLaw()

  /**
   * Right unit law:
   * (monad flatMap unit) == monad
   * */
  def rightUnitLaw(): Unit = {
    val monad: Option[Int] = Option(10)
    assert(monad.flatMap(Some(_)) == monad)
    println("rightUnitLaw check success")
  }

  rightUnitLaw()

  /**
   * Associativity law:
   * ((monad flatMap f) flatMap g) == (monad flatMap (x => f(x) flatMap g))
   * */
  def associativityLaw(): Unit = {
    val monad: Option[Int] = Some(2)
    val left = monad.flatMap(squareFunction).flatMap(incrementFunction)
    val right = monad.flatMap(x => squareFunction(x).flatMap(incrementFunction))
    assert(left == right)
    println("associativityLaw check success")
  }

  associativityLaw()
}
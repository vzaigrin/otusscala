package lectures.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

object FutureRecover extends App {

  def await[T](f: Future[T]): T = Await.result(f, Duration.Inf)

  /** successful vs apply */
  /*  val f: Future[Int] = Future.successful {
      println("thread " + Thread.currentThread().getName)
      throw new Exception("ex1")
      1
    }*/

  /*
    println(f)
  */

  val f2 = Future {
    println("thread " + Thread.currentThread().getName)
    throw new Exception("ex1")
    1
  }

  Await.ready(f2, Duration.Inf)
  println(f2)

  def f: Future[Int] = {
    Future.fromTry(Try(1 / 0))
  }

  Future.successful(1)
    .map[Unit](_ => 1 / 0)
    .flatMap[Unit] { _ =>
      1 / 0;
      Future.successful()
    }


  /** не нарушать контракт */

  //def f: Future[String] = Future.successful(throw new Exception("sdfsdf"))

  //  def fff: Future[String] = Future.failed(throw new Exception("sdfsdf"))

  //  fff


  /** recover */
  /*

    println(await(Future(6 / 2).recover { case e: ArithmeticException => 0 })) // result: 3))

    println(await(
      Future(6 / 0)
        .recover { case e: ArithmeticException => 0 })) // result: 0))

    println(await(
      Future(6 / 0)
        .recover { case e: IllegalArgumentException => 0 })) // result: 0))

  */

  /*  Future(6 / 0).recover { case e: ArithmeticException => 0 } // result: 0
  Future(6 / 0).recover { case e: IllegalArgumentException => 0 } // result: throw exception*/

  /*  val resF = for {
      s <- Future.successful(println("success"))
      _ <- Future(throw new Exception("Boom"))
    } yield s


    println(Future.fromTry(Try(throw new Exception("Boom"))))*/

}



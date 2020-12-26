package lectures.futures

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object FutureConstructors extends App {

  /** successful ~ unit */
  val f: Future[Int] = Future.successful {
    println("thread " + Thread.currentThread().getName)
    1
  }
  println(f)

  val f2 = Future.apply {
    println("thread " + Thread.currentThread().getName)
    1
  }

  /** await */
  Await.ready(f2, Duration.Inf)

  println(f2)

  Future.fromTry(Try(1))
}

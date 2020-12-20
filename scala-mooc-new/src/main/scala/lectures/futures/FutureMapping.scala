package lectures.futures

import java.lang
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object FutureMapping extends App {

  /** for comprehension */
  val xF: Future[Int] = Future.successful(1)

  val xF2: Future[Int] = xF.map { x =>
    println(Thread.currentThread().getName)
    x + 1
  }

  val xF23: Future[Int] = xF.flatMap { x =>
    println(Thread.currentThread().getName)
    Future.successful(x + 1)
  }

  lazy val f1: Future[Int] = Future {
    Thread.sleep(3000); 1
  }
  lazy val f2: Future[Int] = Future {
    Thread.sleep(4000); 1
  }

  lazy val f3: Future[Int] = Future {
    Thread.sleep(5000); 1
  }
  /*
    val fff = for {
      xv <- f1
      xv2 <- f2
      xv3 <- f3
    } yield ()

    Await.ready(fff, Duration.Inf)
    println(fff)
    println("time: " + (System.currentTimeMillis() - startTime))*/

  val yF: Future[Int] = Future.successful(2)

  val startTime = System.currentTimeMillis()
  /** sequence */

  val fer3 = f3.flatMap(x => Future.failed[Int](new Exception("ex3")))
  val fer2 = f2.flatMap(x => Future.failed[Int](new Exception("ex2")))

  val fs: Future[List[Int]] = Future.sequence(List[Future[Int]](
    f1,
    fer2,
    fer3
  ))

  /** traverse */
  val fs2: Future[List[Int]] = Future.traverse(List(1, 2, 3)) {
    x => Future.successful(x)
  }

  Await.ready(fs, Duration.Inf)
  println(fs)
  println("time: " + (System.currentTimeMillis() - startTime))

  /** parallel start, combining */

  /** future eager */

}

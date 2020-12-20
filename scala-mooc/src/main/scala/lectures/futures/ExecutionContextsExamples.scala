package lectures.futures

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object ExecutionContextsExamples extends App {

  def await[T](f: Future[T]): T = Await.result(f, Duration.Inf)

  /** single thread thread pool */

/*    implicit val exx: ExecutionContext =
      ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())*/

  //implicit val exx = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))


  //print(await(resF))

  /** Count runnable in for comprehension */
  val counter = new AtomicInteger(0)

  def maxCount = 2

  implicit val ex = new ExecutionContext {
    val global = scala.concurrent.ExecutionContext.Implicits.global

    override def execute(runnable: Runnable): Unit = {
      counter.incrementAndGet()
      global.execute(runnable)
    }

    override def reportFailure(cause: Throwable): Unit = ???
  }

    def work: Future[Int] = Future {
      println(Thread.currentThread())
      Thread.sleep(1000)
      1
    }

    println("main-" + Thread.currentThread())
    val start: Long = System.currentTimeMillis()
    println("start")

    val f1F = work
    val f2F = work
    val f3F = work

    await(for {
      f1 <- f1F
      f2 <- {
        val f11 = f1 +1
        f2F
      }
      f3 <- {
        val f21 = f2 +1
        f3F
      }
    } yield ())

    println("end - " + (System.currentTimeMillis() - start))

  println("count " + counter.get())


/*
  val resF = for {
    s1 <- Future.successful("1")
  } yield s1 + "2"
  print(await(resF))
*/

}

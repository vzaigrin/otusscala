package homeworks.futures

import homeworks.futures.task_futures_sequence.fullSequence
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class futures_sequence_test extends AnyFlatSpec {

  def await[A](future: Future[A]): A = Await.result(future, Duration.Inf)

  "sequence" should "return list of results" in {
    val ex1 = new Exception("ex1")
    val ex2 = new Exception("ex2")
    val failed1 = Future.failed(ex1)
    val failed2 = Future.failed(ex2)

    assert(await(fullSequence[Int](List(Future(1), Future(2), Future(3)))) === (List(1, 2, 3), List()))
    assert(await(fullSequence[Int](List(Future(1), failed1, failed2))) === (List(1), List(ex1, ex2)))
    assert(await(fullSequence[Int](List(failed1, failed2))) === (List(), List(ex1, ex2)))
  }

}
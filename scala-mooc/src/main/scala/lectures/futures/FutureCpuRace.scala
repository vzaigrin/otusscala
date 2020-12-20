package lectures.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FutureCpuRace extends App {

  /** Вспомогательная метод для измерения времени выполнения */
  def timer(name: String, func: () => Unit): Unit = {
    val start = System.currentTimeMillis()
    func()
    println(s"elapsed time for $name - ${System.currentTimeMillis() - start}")
  }

  /** Метод эмулирующий cpu нагрузку */
  def cpuBoundMethod = {
    val start = System.currentTimeMillis()
    while ((System.currentTimeMillis() - start) < load) {}
    println(Thread.currentThread())
  }

  /** При каком значении параметра load время параллельная обработка выполнится быстрее??? */
  def load = 100

  /** Вариант паралельного запуска в нескольких тредах */
  timer("parallel", () => Await.result(
    Future.sequence(
      List.fill(5)(Future(cpuBoundMethod))
    ), Duration.Inf
  ))

  /** Выполение на одном треде */
  timer("sequential", () => (1 to 5).foreach(_ => cpuBoundMethod))
}

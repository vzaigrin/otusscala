package homeworks.collections

import homeworks.HomeworksUtils.TaskSyntax
import scala.annotation.tailrec

object task_seq_riddle {

  /**
   * Рассмотрим последовательность с числами:
   *
   * 1
   * 1 1
   * 2 1
   * 1 2 1 1
   * 1 1 1 2 2 1
   * 3 1 2 2 1 1
   * ...........
   *
   * 1. Реализуйте функцию генерирующую след последовательность из текущей
   * */

  def nextLine(currentLine: List[Int]): List[Int] = {
    // Вспомогательная функция
    // Накапливает количество повторов "головы" списка
    @tailrec
    def acc(result: List[Int], cn: (Int, Int), line: List[Int]): List[Int] = {
      line.headOption match {
        case Some(h) => if (h == cn._2) acc(result, (cn._1 + 1, cn._2), line.tail)
        else acc(result ++ List(cn._1, cn._2), (1, h), line.tail)
        case None => result ++ List(cn._1, cn._2)
      }
    }

    if (currentLine.isEmpty) List()
    else acc(List(), (1, currentLine.head), currentLine.tail)
  }

  /**
   * 2. Реализуйте ленивый список, который генерирует данную последовательность
   * Hint: см. LazyList.cons
   *
   * lazy val funSeq: LazyList[List[Int]]  ...
   *
   */

  val funSeq: LazyList[List[Int]] = {
    // Вспомогательная функция
    // Организует итерацию
    def loop(line: List[Int]): LazyList[List[Int]] = line #:: loop(nextLine(line))
    loop(List(1))
  }
}

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

/*
  // Вариант 1
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
*/
/*
  // Вариант 2
  def nextLine(currentLine: List[Int]): List[Int] = {
    currentLine.headOption match {
      case None => List()
      case Some(head) =>
        val fold = currentLine.tail.foldLeft((List[Int](), (1, head))) { (acc, x) =>
          if (acc._2._2 == x) (acc._1, (acc._2._1 + 1, x))
          else (acc._1 ++ List(acc._2._1, acc._2._2), (1, x))
        }
        fold._1 ++ List(fold._2._1, fold._2._2)
    }
  }
*/
  // Вариант 3
  def nextLine(currentLine: List[Int]): List[Int] = {
    currentLine.headOption match {
      case None => List()
      case Some(head) =>
        currentLine.tail.foldLeft(List(head, 1)) { (acc, x) =>
          if (acc.head == x) acc.head :: (acc.tail.head + 1) :: acc.tail.tail
          else x :: 1 :: acc
        }.reverse
    }
  }

  /**
   * 2. Реализуйте ленивый список, который генерирует данную последовательность
   * Hint: см. LazyList.cons
   *
   * lazy val funSeq: LazyList[List[Int]]  ...
   *
   */

  val funSeq: LazyList[List[Int]] = List(1) #:: funSeq.map(nextLine)
}

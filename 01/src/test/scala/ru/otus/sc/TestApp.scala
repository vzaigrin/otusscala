package ru.otus.sc

import ru.otus.sc.count.dao.impl.CountDaoImpl

object TestApp {

  class Compute(t: Int) extends Thread {
    override def run(): Unit = {
      (0 until 100) foreach { _ =>
        println(s"$t: ${CountDaoImpl.count}")
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val threads: Seq[Compute] = (0 until 10) map { c => new Compute(c) }
    threads.foreach(_.start())
    threads.foreach(_.join())

    println(s"\nFinal count: ${CountDaoImpl.count}")
  }
}

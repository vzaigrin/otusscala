package ru.otus.sc.count.dao.impl

import ru.otus.sc.count.dao.CountDao

// Имплементация DAO для сервиса Count
// Реализован паттерн Singleton
class CountDaoImplS extends CountDao {
  private object SingletonHolder {
    val instance: CountDao = new CountDaoImplS
  }

  def getInstance: CountDao = SingletonHolder.instance

  private var cnt: Int = 0

  override def count: Int = {
    cnt += 1
    cnt
  }
}

package ru.otus.sc.count.dao.impl

import ru.otus.sc.count.dao.CountDao

// Имплементация DAO для сервиса Count
// Реализован паттерн Singleton
class CountDaoImplS extends CountDao {
  private object SingletonHolder {
    val instance: CountDaoImplS = new CountDaoImplS
  }

  private var cnt: Int = 0

  override def count: Int = {
    def getInstance: CountDaoImplS = SingletonHolder.instance
    getInstance.cnt += 1
    getInstance.cnt
  }
}

package ru.otus.sc.count.dao.impl

import ru.otus.sc.count.dao.CountDao

// Имплементация DAO для сервиса Count
// Реализован паттерн Singleton
class CountDaoImplS extends CountDao {
  private var instance: CountDaoImplS = _

  private def getInstance: CountDaoImplS = {
    if (instance == null) {
      instance = new CountDaoImplS()
    }
    instance
  }

  private var cnt: Int = 0

  override def count: Int = {
    getInstance.cnt += 1
    getInstance.cnt
  }
}

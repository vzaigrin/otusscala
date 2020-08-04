package ru.otus.sc.count.dao.impl

import ru.otus.sc.count.dao.CountDao

// Имплементация DAO для сервиса Count
object CountDaoImpl extends CountDao {
  private var cnt: Int = 0

  override def count: Int = {
    cnt += 1
    cnt
  }
}

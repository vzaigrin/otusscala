package ru.otus.sc.key.dao.impl

import ru.otus.sc.key.dao.KeyDao

// Имплементация DAO для сервиса Key
// Подготовлен Mqp
object KeyDaoImpl extends KeyDao {
  private val keys: Map[Int, String] = Map(
    (0, "zero"),
    (1, "one"),
    (2, "two"),
    (3, "three"),
    (4, "four"),
    (5, "five"),
    (6, "six"),
    (7, "seven"),
    (8, "eight"),
    (9, "nine")
  )

  override def key(input: Int): String = {
    if (keys.contains(input)) keys(input)
    else "Illegal key"
  }
}

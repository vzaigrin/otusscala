package ru.otus.sc.reverse.dao.impl

import ru.otus.sc.reverse.dao.ReverseDao

// Имплементация DAO дяя сервиса Reverse
class ReverseDaoImpl extends ReverseDao {
  override def reversePrefix: String  = ""
  override def reversePostfix: String = ""
}

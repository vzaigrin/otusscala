package ru.otus.sc.echo.dao.impl

import ru.otus.sc.echo.dao.EchoDao

// Имплементация DAO Echo
class EchoDaoImpl extends EchoDao {
  override def echoPrefix: String  = ""
  override def echoPostfix: String = ""
}

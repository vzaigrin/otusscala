package ru.otus.sc.key.dao

// трейт DAO для сервиса Key
trait KeyDao {
  def key(input: Int): String
}

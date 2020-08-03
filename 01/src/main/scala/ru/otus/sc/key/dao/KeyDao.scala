package ru.otus.sc.key.dao

// трейт DAO для Key
trait KeyDao {
  def key(input: Int): String
}

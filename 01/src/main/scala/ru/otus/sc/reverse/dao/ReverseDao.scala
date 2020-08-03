package ru.otus.sc.reverse.dao

// trait DAO дяя сервиса Reverse
trait ReverseDao {
  def reversePrefix: String
  def reversePostfix: String
}

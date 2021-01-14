package ru.otus.sc.route

sealed trait ErrorInfo
case class NotFound(what: String)     extends ErrorInfo
case class Unauthorized(what: String) extends ErrorInfo
case class BadRequest(what: String)   extends ErrorInfo

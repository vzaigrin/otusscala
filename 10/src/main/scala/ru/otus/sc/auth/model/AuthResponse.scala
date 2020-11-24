package ru.otus.sc.auth.model

sealed trait AuthResponse

case class UserValid(token: String)          extends AuthResponse
case class UserInvalid(username: String)     extends AuthResponse
case class TokenValid(token: String)         extends AuthResponse
case class TokenInvalid(token: String)       extends AuthResponse
case class LogoutSuccessful(token: String)   extends AuthResponse
case class LogoutUnsuccessful(token: String) extends AuthResponse

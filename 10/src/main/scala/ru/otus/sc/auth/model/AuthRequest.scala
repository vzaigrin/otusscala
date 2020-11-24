package ru.otus.sc.auth.model

import akka.actor.typed.ActorRef

sealed trait AuthRequest

case class CheckUser(username: String, password: String, replyTo: ActorRef[AuthResponse])
    extends AuthRequest
case class CheckToken(token: String, replyTo: ActorRef[AuthResponse]) extends AuthRequest
case class Logout(token: String, replyTo: ActorRef[AuthResponse])     extends AuthRequest

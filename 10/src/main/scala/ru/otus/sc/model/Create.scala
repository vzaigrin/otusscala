package ru.otus.sc.model

case class CreateRequest[+T <: Entity](entity: T)
case class CreateResponse[+T <: Entity](entity: T)

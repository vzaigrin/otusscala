package ru.otus.sc.model

import java.util.UUID

// Базовый класс для всех сущностей
// Каждая сущность имеет свой Id
abstract class Entity {
  val id: Option[UUID]
}

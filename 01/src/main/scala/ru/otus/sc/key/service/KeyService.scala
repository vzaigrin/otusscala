package ru.otus.sc.key.service

import ru.otus.sc.key.model.{KeyRequest, KeyResponse}

trait KeyService {
  def key(request: KeyRequest): KeyResponse
}

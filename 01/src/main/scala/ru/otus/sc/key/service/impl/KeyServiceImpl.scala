package ru.otus.sc.key.service.impl

import ru.otus.sc.key.dao.KeyDao
import ru.otus.sc.key.model.{KeyRequest, KeyResponse}
import ru.otus.sc.key.service.KeyService

class KeyServiceImpl(dao: KeyDao) extends KeyService {
  override def key(request: KeyRequest): KeyResponse = KeyResponse(dao.key(request.request))
}

package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.model.Record
import ru.otus.sc.route.impl.RecordRouter
import ru.otus.sc.service.Service

class RecordRouterTest extends AnyFreeSpec with ScalatestRouteTest with MockFactory {
  "Methods tests" - {
    "route" in {
      val srv    = mock[Service[Record]]
      val router = new RecordRouter("", srv)
      val uuid   = UUID.randomUUID()

      Get(s"/record/?id=$uuid") ~> router.route ~> check {
        handled shouldBe false
      }
    }
  }
}

package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.service.RoleService

class RoleRouterTest extends AnyFreeSpec with ScalatestRouteTest with MockFactory {
  "Methods tests" - {
    "route" in {
      val srv    = mock[RoleService]
      val router = new RoleRouter(srv)
      val uuid   = UUID.randomUUID()

      Get(s"/role/?id=$uuid") ~> router.route ~> check {
        handled shouldBe false
      }
    }
  }
}

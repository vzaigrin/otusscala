package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.model.Author
import ru.otus.sc.route.impl.AuthorRouter
import ru.otus.sc.service.Service

class AuthorRouterTest extends AnyFreeSpec with ScalatestRouteTest with MockFactory {
  "Methods tests" - {
    "route" in {
      val srv    = mock[Service[Author]]
      val router = new AuthorRouter("", srv)
      val uuid   = UUID.randomUUID()

      Get(s"/author/?id=$uuid") ~> router.route ~> check {
        handled shouldBe false
      }
    }
  }
}

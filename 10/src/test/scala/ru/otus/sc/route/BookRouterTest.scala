package ru.otus.sc.route

import java.util.UUID
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.model.Book
import ru.otus.sc.route.impl.BookRouter
import ru.otus.sc.service.Service

class BookRouterTest extends AnyFreeSpec with ScalatestRouteTest with MockFactory {
  "Methods tests" - {
    "route" in {
      val srv    = mock[Service[Book]]
      val router = new BookRouter("", srv)
      val uuid   = UUID.randomUUID()

      Get(s"/book/?id=$uuid") ~> router.route ~> check {
        handled shouldBe false
      }
    }
  }
}

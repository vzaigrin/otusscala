package ru.otus.sc.json

import org.scalatest.freespec.AnyFreeSpec
import org.scalacheck.ScalacheckShapeless._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.{JsSuccess, Json}
import ru.otus.sc.model.book.Book
import BookJsonProtocol._

class BookJsonProtocolTest extends AnyFreeSpec with ScalaCheckDrivenPropertyChecks {
  "Methods tests" - {
    "bookFormat" in {
      forAll { book: Book =>
        Json.fromJson[Book](Json.toJson(book)) shouldBe (JsSuccess(book))
      }
    }
  }
}

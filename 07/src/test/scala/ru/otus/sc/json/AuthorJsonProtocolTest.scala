package ru.otus.sc.json

import org.scalatest.freespec.AnyFreeSpec
import org.scalacheck.ScalacheckShapeless._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.{JsSuccess, Json}
import ru.otus.sc.model.author.Author
import AuthorJsonProtocol._

class AuthorJsonProtocolTest extends AnyFreeSpec with ScalaCheckDrivenPropertyChecks {
  "Methods tests" - {
    "authorFormat" in {
      forAll { author: Author =>
        Json.fromJson[Author](Json.toJson(author)) shouldBe (JsSuccess(author))
      }
    }
  }
}

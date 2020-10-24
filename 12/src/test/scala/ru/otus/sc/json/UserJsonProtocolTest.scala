package ru.otus.sc.json

import org.scalatest.freespec.AnyFreeSpec
import org.scalacheck.ScalacheckShapeless._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.{JsSuccess, Json}
import ru.otus.sc.model.user.User
import UserJsonProtocol._

class UserJsonProtocolTest extends AnyFreeSpec with ScalaCheckDrivenPropertyChecks {
  "Methods tests" - {
    "userFormat" in {
      forAll { user: User =>
        Json.fromJson[User](Json.toJson(user)) shouldBe (JsSuccess(user))
      }
    }
  }
}

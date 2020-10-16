package ru.otus.sc.json

import org.scalatest.freespec.AnyFreeSpec
import org.scalacheck.ScalacheckShapeless._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.{JsSuccess, Json}
import ru.otus.sc.model.role.Role
import RoleJsonProtocol._

class RoleJsonProtocolTest extends AnyFreeSpec with ScalaCheckDrivenPropertyChecks {
  "Methods tests" - {
    "roleFormat" in {
      forAll { role: Role =>
        Json.fromJson[Role](Json.toJson(role)) shouldBe (JsSuccess(role))
      }
    }
  }
}

package ru.otus.sc.json

import play.api.libs.json.{Format, Json, OFormat}
import ru.otus.sc.model.user.{User, UpdateUserResponse}
import RoleJsonProtocol._

trait UserJsonProtocol extends AdtProtocol {
  implicit lazy val userFormat: OFormat[User] = Json.format

  implicit lazy val updateUserResponseFormat: Format[UpdateUserResponse] = {
    implicit val updatedFormat: OFormat[UpdateUserResponse.Updated]   = Json.format
    implicit val notFoundFormat: OFormat[UpdateUserResponse.NotFound] = Json.format
    implicit val cantUpdateUserWithoutIdFormat
        : OFormat[UpdateUserResponse.CantUpdateUserWithoutId.type] =
      objectFormat(UpdateUserResponse.CantUpdateUserWithoutId)

    adtFormat("$type")(
      adtCase[UpdateUserResponse.Updated]("Updated"),
      adtCase[UpdateUserResponse.NotFound]("NotFound"),
      adtCase[UpdateUserResponse.CantUpdateUserWithoutId.type]("CantUpdateUserWithoutId")
    )
  }
}

object UserJsonProtocol extends UserJsonProtocol

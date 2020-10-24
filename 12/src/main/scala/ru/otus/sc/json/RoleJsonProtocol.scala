package ru.otus.sc.json

import play.api.libs.json.{Format, Json, OFormat}
import ru.otus.sc.model.role.{Role, UpdateRoleResponse}

trait RoleJsonProtocol extends AdtProtocol {
  implicit lazy val roleFormat: OFormat[Role] = Json.format

  implicit lazy val updateRoleResponseFormat: Format[UpdateRoleResponse] = {
    implicit val updatedFormat: OFormat[UpdateRoleResponse.Updated]   = Json.format
    implicit val notFoundFormat: OFormat[UpdateRoleResponse.NotFound] = Json.format
    implicit val cantUpdateRoleWithoutIdFormat
        : OFormat[UpdateRoleResponse.CantUpdateRoleWithoutId.type] =
      objectFormat(UpdateRoleResponse.CantUpdateRoleWithoutId)

    adtFormat("$type")(
      adtCase[UpdateRoleResponse.Updated]("Updated"),
      adtCase[UpdateRoleResponse.NotFound]("NotFound"),
      adtCase[UpdateRoleResponse.CantUpdateRoleWithoutId.type]("CantUpdateRoleWithoutId")
    )
  }
}

object RoleJsonProtocol extends RoleJsonProtocol

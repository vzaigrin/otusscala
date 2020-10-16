package ru.otus.sc.json

import play.api.libs.json.{Format, Json, OFormat}
import ru.otus.sc.model.author.{Author, UpdateAuthorResponse}

trait AuthorJsonProtocol extends AdtProtocol {
  implicit lazy val authorFormat: OFormat[Author] = Json.format

  implicit lazy val updateAuthorResponseFormat: Format[UpdateAuthorResponse] = {
    implicit val updatedFormat: OFormat[UpdateAuthorResponse.Updated]   = Json.format
    implicit val notFoundFormat: OFormat[UpdateAuthorResponse.NotFound] = Json.format
    implicit val cantUpdateAuthorWithoutIdFormat
        : OFormat[UpdateAuthorResponse.CantUpdateAuthorWithoutId.type] =
      objectFormat(UpdateAuthorResponse.CantUpdateAuthorWithoutId)

    adtFormat("$type")(
      adtCase[UpdateAuthorResponse.Updated]("Updated"),
      adtCase[UpdateAuthorResponse.NotFound]("NotFound"),
      adtCase[UpdateAuthorResponse.CantUpdateAuthorWithoutId.type]("CantUpdateAuthorWithoutId")
    )
  }
}

object AuthorJsonProtocol extends AuthorJsonProtocol

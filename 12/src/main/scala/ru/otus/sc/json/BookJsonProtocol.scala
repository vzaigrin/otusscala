package ru.otus.sc.json

import play.api.libs.json.{Format, Json, OFormat}
import ru.otus.sc.model.book.{Book, UpdateBookResponse}
import AuthorJsonProtocol._

trait BookJsonProtocol extends AdtProtocol {
  implicit lazy val bookFormat: OFormat[Book] = Json.format

  implicit lazy val updateBookResponseFormat: Format[UpdateBookResponse] = {
    implicit val updatedFormat: OFormat[UpdateBookResponse.Updated]   = Json.format
    implicit val notFoundFormat: OFormat[UpdateBookResponse.NotFound] = Json.format
    implicit val cantUpdateBookWithoutIdFormat
        : OFormat[UpdateBookResponse.CantUpdateBookWithoutId.type] =
      objectFormat(UpdateBookResponse.CantUpdateBookWithoutId)

    adtFormat("$type")(
      adtCase[UpdateBookResponse.Updated]("Updated"),
      adtCase[UpdateBookResponse.NotFound]("NotFound"),
      adtCase[UpdateBookResponse.CantUpdateBookWithoutId.type]("CantUpdateBookWithoutId")
    )
  }
}

object BookJsonProtocol extends BookJsonProtocol

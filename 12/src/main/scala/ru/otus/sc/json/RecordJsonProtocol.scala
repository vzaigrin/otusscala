package ru.otus.sc.json

import java.sql.Timestamp
import play.api.libs.json._
import ru.otus.sc.model.record.{Record, UpdateRecordResponse}
import java.text.SimpleDateFormat
import AuthorJsonProtocol._
import BookJsonProtocol._
import RoleJsonProtocol._
import UserJsonProtocol._

trait RecordJsonProtocol extends AdtProtocol {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    def reads(json: JsValue): JsSuccess[Timestamp] = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp): JsString = JsString(format.format(ts))
  }

  implicit lazy val recordFormat: OFormat[Record] = Json.format

  implicit lazy val updateRecordResponseFormat: Format[UpdateRecordResponse] = {
    implicit val updatedFormat: OFormat[UpdateRecordResponse.Updated]   = Json.format
    implicit val notFoundFormat: OFormat[UpdateRecordResponse.NotFound] = Json.format
    implicit val cantUpdateRecordWithoutIdFormat
        : OFormat[UpdateRecordResponse.CantUpdateRecordWithoutId.type] =
      objectFormat(UpdateRecordResponse.CantUpdateRecordWithoutId)

    adtFormat("$type")(
      adtCase[UpdateRecordResponse.Updated]("Updated"),
      adtCase[UpdateRecordResponse.NotFound]("NotFound"),
      adtCase[UpdateRecordResponse.CantUpdateRecordWithoutId.type]("CantUpdateRecordWithoutId")
    )
  }
}

object RecordJsonProtocol extends RecordJsonProtocol

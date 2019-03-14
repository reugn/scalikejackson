package reug.scalikejackson.commons.utils

import play.api.libs.json._

object EnumUtils {
    def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = Reads {
        case JsString(s) =>
            try {
                JsSuccess(enum.withName(s))
            } catch {
                case _: NoSuchElementException =>
                    JsError(s"Enumeration '${enum.getClass}', does not contain the value: '$s'")
            }
        case _ => JsError("String value expected")
    }

    implicit def enumWrites[E <: Enumeration]: Writes[E#Value] =
        Writes((v: E#Value) => JsString(v.toString))

    implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
        Format(enumReads(enum), enumWrites)
    }
}

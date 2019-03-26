package reug.scalikejackson.test.models

import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reug.scalikejackson.ScalaJacksonFormatter
import reug.scalikejackson.play.{Json => LJson}

case class MockStruct(
                         i: Int,
                         s: String,
                         b: Option[Boolean]
                     ) {

}

object MockStruct {

    val reader: StdDeserializer[MockStruct] = LJson.reads[MockStruct](
        ("in", classOf[Int]),
        ("sn", classOf[String]),
        ("bn", Option(classOf[Boolean]))
    )

    val writer: StdSerializer[MockStruct] = LJson.writes[MockStruct](
        p => (
            ("in", classOf[Int], p.i),
            ("sn", classOf[String], p.s),
            ("bn", Option(classOf[Boolean]), p.b)
        )
    )

    implicit val formatter: ScalaJacksonFormatter[MockStruct] =
        LJson.format[MockStruct] + (writer, reader)

    val mock_reads: Reads[MockStruct] = (
        (__ \ "in").read[Int] and
            (__ \ "sn").read[String] and
            (__ \ "bn").readNullable[Boolean]
        ) (MockStruct.apply _)

    val mock_writes: Writes[MockStruct] = (
        (__ \ "in").write[Int] and
            (__ \ "sn").write[String] and
            (__ \ "bn").writeNullable[Boolean]
        ) (unlift(MockStruct.unapply))

    implicit val play_reader: Reads[MockStruct] = Json.reads[MockStruct] or mock_reads
    implicit val play_writer: Writes[MockStruct] = Json.writes[MockStruct]
}

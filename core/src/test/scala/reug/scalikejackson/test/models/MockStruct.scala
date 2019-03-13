package reug.scalikejackson.test.models

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.{BooleanNode, IntNode}
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode, SerializerProvider}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reug.scalikejackson.{ScalaJacksonFormat, ScalaJacksonFormatter}

case class MockStruct(
                         i: Int,
                         s: String,
                         b: Option[Boolean]
                     ) {

}

object MockStruct {

    class MockStructSer(t: Class[MockStruct]) extends StdSerializer[MockStruct](t) {
        override def serialize(value: MockStruct, gen: JsonGenerator, provider: SerializerProvider): Unit = {
            gen.writeStartObject()
            gen.writeNumberField("in", value.i)
            gen.writeStringField("sn", value.s)
            if (value.b.isDefined) gen.writeBooleanField("bn", value.b.get)
            gen.writeEndObject()
        }
    }

    class MockStructDe(t: Class[MockStruct]) extends StdDeserializer[MockStruct](t) {
        override def deserialize(p: JsonParser, ctxt: DeserializationContext): MockStruct = {
            val node = p.getCodec.readTree[JsonNode](p)
            val i = node.get("in").asInstanceOf[IntNode].intValue()
            val s = node.get("sn").asText
            val b = Option(node.get("bn")).map(_.asInstanceOf[BooleanNode].booleanValue())
            MockStruct(i, s, b)
        }
    }

    implicit val formatter: ScalaJacksonFormatter[MockStruct] =
        new ScalaJacksonFormat[MockStruct] + (new MockStructSer(classOf[MockStruct]), new MockStructDe(classOf[MockStruct]))

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

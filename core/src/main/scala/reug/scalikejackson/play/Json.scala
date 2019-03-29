package reug.scalikejackson.play

import java.io.{IOException, InputStream}

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node._
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson._
import reug.scalikejackson.`macro`.JsonSerdeMacro
import reug.scalikejackson.utils.JsArrayIterator

import scala.collection.JavaConverters._
import scala.language.experimental.macros
import scala.language.postfixOps
import scala.reflect.ClassTag

object Json {

    private val json_node_factory = JsonNodeFactory.withExactBigDecimals(true)

    @inline def obj(fields: (String, JsonNode)*): ObjectNode = {
        new ObjectNode(json_node_factory).setAll(fields.toMap asJava).a[ObjectNode]
    }

    @inline def arr(items: JsonNode*): ArrayNode = {
        new ArrayNode(json_node_factory).addAll(items asJava)
    }

    @inline def parse(str: String): JsonNode = {
        str.toJson
    }

    @inline def stringify(jsNode: JsonNode): String = {
        jsNode.toString
    }

    @throws(classOf[IllegalArgumentException])
    @throws(classOf[IOException])
    @inline def iter[T: ScalaJacksonReader : ClassTag](is: InputStream): Iterator[T] = {
        new JsArrayIterator[T](is)
    }

    /**
      * Creates `StdDeserializer[T]` in compile time
      *
      * {{{
      *     val reader: StdDeserializer[MockStruct] = Json.reads[MockStruct](
      *         ("in", classOf[Int]), //can be simple Int value like 3
      *         ("sn", classOf[String]),
      *         ("bn", Option(classOf[Boolean]))
      *     )
      * }}}
      *
      * ===Parameter tuple structure===
      * ("in / inn", classOf[Int], 3)
      * |           |             |_ default value
      * |           |_ classOf json value(can be optional)
      * |_ json path to read from
      *
      * @param p varargs tuple
      * @tparam T generic class type
      * @return StdDeserializer[T]
      */
    def reads[T](p: Any*): StdDeserializer[T] = macro JsonSerdeMacro.reads[T]

    /**
      * Creates `StdSerializer[T]` in compile time
      *
      * {{{
      *     val writer: StdSerializer[MockStruct] = Json.writes[MockStruct](
      *         p => (
      *             ("in", classOf[Int], p.i),
      *             ("sn", classOf[String], p.s),
      *             ("bn", Option(classOf[Boolean]), p.b)
      *         )
      *     )
      * }}}
      *
      * ===Parameter tuple structure===
      * ("in / inn", classOf[Int], p.i)
      * |           |             |_ instance parameter to write
      * |           |_ classOf instance value(can be optional)
      * |_ json path to write to
      *
      * @param p : T => Any; where T is a class instance, Any is a varargs tuple
      * @tparam T generic class type
      * @return StdSerializer[T]
      */
    def writes[T](p: T => Any): StdSerializer[T] = macro JsonSerdeMacro.writes[T]

    /**
      * Creates new `ScalaJacksonRead[T]` for generic T type
      *
      * @param config ObjectMapper configuration objects.
      *               See supported [[ScalaJacksonParser.parseConfiguration]]
      * @tparam T generic class type
      * @return ScalaJacksonReader[T]
      */
    def read[T: ClassTag](config: Any*): ScalaJacksonReader[T] = new ScalaJacksonRead[T](config: _*)

    /**
      * Creates new `ScalaJacksonWrite[T]` for generic T type
      *
      * @param config ObjectMapper configuration objects.
      *               See supported [[ScalaJacksonParser.parseConfiguration]]
      * @tparam T generic class type
      * @return ScalaJacksonWriter[T]
      */
    def write[T: ClassTag](config: Any*): ScalaJacksonWriter[T] = new ScalaJacksonWrite[T](config: _*)

    /**
      * Creates new `ScalaJacksonFormat[T]` for generic T type
      *
      * @param config ObjectMapper configuration objects.
      *               See supported [[ScalaJacksonParser.parseConfiguration]]
      * @tparam T generic class type
      * @return ScalaJacksonFormatter[T]
      */
    def format[T: ClassTag](config: Any*): ScalaJacksonFormatter[T] = new ScalaJacksonFormat[T](config: _*)
}

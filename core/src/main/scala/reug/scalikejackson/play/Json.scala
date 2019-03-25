package reug.scalikejackson.play

import java.io.{IOException, InputStream}

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.ScalaJacksonReader
import reug.scalikejackson.utils.JsArrayIterator

import scala.collection.JavaConverters._
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
}

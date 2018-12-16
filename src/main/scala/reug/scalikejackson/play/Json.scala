package reug.scalikejackson.play

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import reug.scalikejackson.ScalaJacksonImpl._

import scala.collection.JavaConverters._
import scala.language.postfixOps

object Json {

    private val json_node_factory = JsonNodeFactory.withExactBigDecimals(true)

    @inline
    def obj(fields: (String, JsonNode)*): JsonNode = {
        new ObjectNode(json_node_factory).setAll(fields.toMap asJava)
    }

    @inline
    def arr(items: JsonNode*): JsonNode = {
        new ArrayNode(json_node_factory).addAll(items asJava)
    }

    @inline
    def parse(str: String): JsonNode = {
        str.toJson
    }

    @inline
    def stringify(jsNode: JsonNode): String = {
        jsNode.toString
    }
}

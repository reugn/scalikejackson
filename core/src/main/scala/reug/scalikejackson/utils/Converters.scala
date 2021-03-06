package reug.scalikejackson.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._

import scala.language.{implicitConversions, postfixOps}

object Converters {

    implicit def ~>(f: Int): JsonNode = new IntNode(f)
    implicit def ~>(f: Long): JsonNode = new LongNode(f)
    implicit def ~>(f: String): JsonNode = new TextNode(f)
    implicit def ~>(f: Boolean): JsonNode = BooleanNode.valueOf(f)
    implicit def ~>(f: ArrayNode): JsonNode = f.asInstanceOf[JsonNode]

    implicit def ~>[T](opt: Option[T])(implicit ->> : T ~> JsonNode): JsonNode = {
        opt.fold[JsonNode](NullNode.getInstance)(->> >>)
    }

    abstract class ~>[F, T <: JsonNode] {
        def >>(f: F): T
    }

    implicit object IntConverter extends ~>[Int, JsonNode] {
        override def >>(f: Int): JsonNode = new IntNode(f)
    }

    implicit object LongConverter extends ~>[Long, JsonNode] {
        override def >>(f: Long): JsonNode = new LongNode(f)
    }

    implicit object StringConverter extends ~>[String, JsonNode] {
        override def >>(f: String): JsonNode = new TextNode(f)
    }

    implicit object BoolConverter extends ~>[Boolean, JsonNode] {
        override def >>(f: Boolean): JsonNode = BooleanNode.valueOf(f)
    }

    implicit object JsonNodeConverter extends ~>[JsonNode, JsonNode] {
        override def >>(f: JsonNode): JsonNode = f
    }

    implicit object ArrayNodeConverter extends ~>[ArrayNode, JsonNode] {
        override def >>(f: ArrayNode): JsonNode = f.asInstanceOf[JsonNode]
    }

}
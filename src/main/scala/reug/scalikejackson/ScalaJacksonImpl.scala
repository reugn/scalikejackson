package reug.scalikejackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{NullNode, ObjectNode}

import scala.annotation.implicitNotFound
import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.reflect.ClassTag
import scala.util.Try

object ScalaJacksonImpl {
    implicit val jsonNodeFormat: ScalaJacksonFormatter[JsonNode] = new ScalaJacksonFormat[JsonNode]

    //primitives' formatters
    implicit val jsonNodeFormatString: ScalaJacksonFormatter[String] = new ScalaJacksonFormat[String]
    implicit val jsonNodeFormatInt: ScalaJacksonFormatter[Int] = new ScalaJacksonFormat[Int]
    implicit val jsonNodeFormatLong: ScalaJacksonFormatter[Long] = new ScalaJacksonFormat[Long]
    implicit val jsonNodeFormatBool: ScalaJacksonFormatter[Boolean] = new ScalaJacksonFormat[Boolean]

    implicit class StringImpl(val str: String) extends AnyVal {
        @implicitNotFound("Could not find a ScalaJacksonReader[${T}] in scope.")
        def read[T: ScalaJacksonReader : ClassTag]: T = {
            implicitly[ScalaJacksonReader[T]].read(str)
        }

        def toJson(implicit jsonNodeRead: ScalaJacksonReader[JsonNode]): JsonNode = {
            jsonNodeRead.toJson(str)
        }
    }

    @implicitNotFound("Could not find a ScalaJacksonWriter[${T}] in scope.")
    implicit class JsonImpl[T: ScalaJacksonWriter : ClassTag](val pr: T) {
        def write: String = {
            implicitly[ScalaJacksonWriter[T]].write(pr)
        }
    }

    implicit class JsonNodeImpl(val node: JsonNode) extends AnyVal {
        def asSeq[T: ScalaJacksonReader : ClassTag]: Seq[T] = {
            if (node.isArray) {
                node.iterator().asScala.toSeq.map {
                    el => el.as[T]
                }
            } else {
                throw new UnsupportedOperationException("JsonNode is not an array")
            }
        }

        def asSeqOpt[T: ScalaJacksonReader : ClassTag]: Option[Seq[T]] = {
            Try {
                node.asSeq[T]
            } toOption
        }

        def as[@specialized(Specializable.Everything) T: ScalaJacksonReader : ClassTag]: T = {
            implicitly[ScalaJacksonReader[T]] convert node
        }

        def asOpt[T: ScalaJacksonReader : ClassTag]: Option[T] = {
            Try {
                node.as[T]
            } toOption
        }

        def a[T <: JsonNode]: T = node.asInstanceOf[T]

        def \(key: String): JsLookupResult = {
            node get key match {
                case _: NullNode | null =>
                    JsLookupResult(None)
                case js_node =>
                    JsLookupResult(Some(js_node))
            }
        }

        def \\(key: String): Seq[JsonNode] = {
            node.findValues(key).asScala
        }
    }

    implicit class ObjectNodeImpl(val source: ObjectNode) extends AnyVal {
        def ++(target: ObjectNode): ObjectNode = {
            if (target == null) source
            else {
                val result = source.deepCopy
                val target_list = target.fieldNames.asScala.toList
                for (item <- target_list) {
                    result.set(item, target get item)
                }
                result
            }
        }

        def -(key: String): ObjectNode = {
            val result = source.deepCopy
            result.remove(key)
            result
        }

        def filter(cond: JsonNode => Boolean = _.isNull): ObjectNode = {
            val result = source.deepCopy.removeAll()
            val fields = source.fieldNames.asScala.toList
            for (f <- fields) {
                val item = source get f
                if (!cond(item)) result.set(f, item)
            }
            result
        }
    }

    implicit class SeqImpl[T: ScalaJacksonWriter : ClassTag](val seq: Seq[T]) {
        def write: String = {
            val buffer = StringBuilder.newBuilder
            buffer.append("[")
            seq.foreach {
                el =>
                    buffer.append(implicitly[ScalaJacksonWriter[T]].write(el) + ",")
            }
            if (buffer.length > 1)
                buffer.dropRight(1).append("]").toString()
            else
                buffer.toString()
        }
    }

}

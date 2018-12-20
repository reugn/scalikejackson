package reug.scalikejackson

import com.fasterxml.jackson.databind.JsonNode
import reug.scalikejackson.ScalaJacksonImpl._

import scala.reflect.ClassTag

case class JsLookupResult(node: Option[JsonNode]) {
    self =>
    def \(key: String): JsLookupResult = {
        node match {
            case Some(jsonNode) =>
                jsonNode \ key
            case None =>
                self
        }
    }

    @throws(classOf[NoSuchElementException])
    def as[@specialized(Specializable.Everything) T: ScalaJacksonReader : ClassTag]: T = {
        node.get.toString.read[T]
    }

    def asOpt[@specialized(Specializable.Everything) T: ScalaJacksonReader : ClassTag]: Option[T] = {
        node.map(v => v.toString.read[T])
    }

    @throws(classOf[NoSuchElementException])
    def asSeq[T: ScalaJacksonReader : ClassTag]: Seq[T] = {
        node.get.asSeq[T]
    }

    def asSeqOpt[T: ScalaJacksonReader : ClassTag]: Option[Seq[T]] = {
        node.flatMap(v => v.asSeqOpt[T])
    }
}

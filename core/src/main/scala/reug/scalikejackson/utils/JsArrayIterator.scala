package reug.scalikejackson.utils

import java.io.InputStream

import com.fasterxml.jackson.core.JsonToken
import reug.scalikejackson.ScalaJacksonReader

import scala.reflect.ClassTag

class JsArrayIterator[T: ScalaJacksonReader : ClassTag](is: InputStream) extends Iterator[T] {
    private val clazz = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]

    private var next_element: T = <<

    private lazy val json_parser = {
        val mapper = implicitly[ScalaJacksonReader[T]].mappers.last
        val jsonFactory = mapper.getFactory
        val parser = jsonFactory.createParser(is)
        require(parser.nextToken == JsonToken.START_ARRAY, "JSON array input required")
        parser
    }

    private def << : T = {
        val next = json_parser.nextToken()
        if (next != JsonToken.END_ARRAY) {
            require(next == JsonToken.START_OBJECT)
            json_parser.readValueAs(clazz)
        } else null.asInstanceOf[T]
    }

    override def hasNext: Boolean = {
        next_element != null
    }

    override def next(): T = {
        val curr = next_element
        next_element = <<
        curr
    }
}

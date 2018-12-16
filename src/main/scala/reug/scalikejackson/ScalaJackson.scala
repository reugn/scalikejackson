package reug.scalikejackson

import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.collection.mutable
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.Try

sealed trait ScalaJacksonParser[T] {
    protected val mappers: mutable.MutableList[ObjectMapper] = mutable.MutableList[ObjectMapper]()
    mappers += (new ObjectMapper() with ScalaObjectMapper)
        .registerModule(DefaultScalaModule)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)

    def registerSerializer(ser: StdSerializer[T])(implicit ctag: ClassTag[T]): ScalaJacksonParser[T] = {
        val clazz = ctag.runtimeClass.asInstanceOf[Class[T]]
        (new ObjectMapper() with ScalaObjectMapper).registerModule((new SimpleModule).addSerializer(clazz, ser)) +=: mappers
        this
    }

    def registerDeserializer(de: StdDeserializer[T])(implicit ctag: ClassTag[T]): ScalaJacksonParser[T] = {
        val clazz = ctag.runtimeClass.asInstanceOf[Class[T]]
        (new ObjectMapper() with ScalaObjectMapper).registerModule((new SimpleModule).addDeserializer(clazz, de)) +=: mappers
        this
    }

    def registerSerde(serde: (StdSerializer[T], StdDeserializer[T]))(implicit ctag: ClassTag[T]): ScalaJacksonParser[T] = {
        val (ser, de) = serde
        val clazz = ctag.runtimeClass.asInstanceOf[Class[T]]
        (new ObjectMapper() with ScalaObjectMapper).
            registerModule((new SimpleModule).addSerializer(clazz, ser).addDeserializer(clazz, de)) +=: mappers
        this
    }

    protected def _serdeException(serde: String)(implicit ctag: ClassTag[T]): Nothing = {
        val cn = implicitly[ClassTag[T]].runtimeClass.getSimpleName
        throw new Exception(s"No $cn $serde found for json input")
    }
}

trait ScalaJacksonReader[T] extends ScalaJacksonParser[T] {
    def read(str: String)(implicit ctag: ClassTag[T]): T = {
        val clazz = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
        for (i <- mappers.indices) {
            Try(mappers(i).readValue(str, clazz)).toOption.map(return _)
        }
        _serdeException("deserializer")
    }

    def toJson(str: String)(implicit ctag: ClassTag[T]): JsonNode = {
        for (i <- mappers.indices) {
            Try(mappers(i).readTree(str)).toOption.map(return _)
        }
        _serdeException("deserializer")
    }

    def +(de: StdDeserializer[T])(implicit ctag: ClassTag[T]): ScalaJacksonReader[T] =
        registerDeserializer(de).asInstanceOf[ScalaJacksonReader[T]]
}

trait ScalaJacksonWriter[T] extends ScalaJacksonParser[T] {
    def write(obj: T)(implicit ctag: ClassTag[T]): String = {
        for (i <- mappers.indices) {
            Try(mappers(i).writeValueAsString(obj)).toOption.map(return _)
        }
        _serdeException("serializer")
    }

    def +(ser: StdSerializer[T])(implicit ctag: ClassTag[T]): ScalaJacksonWriter[T] =
        registerSerializer(ser).asInstanceOf[ScalaJacksonWriter[T]]
}

trait ScalaJacksonFormatter[T] extends ScalaJacksonReader[T] with ScalaJacksonWriter[T] {
    override def +(ser: StdSerializer[T])(implicit ctag: ClassTag[T]): ScalaJacksonFormatter[T] =
        registerSerializer(ser).asInstanceOf[ScalaJacksonFormatter[T]]

    override def +(de: StdDeserializer[T])(implicit ctag: ClassTag[T]): ScalaJacksonFormatter[T] =
        registerDeserializer(de).asInstanceOf[ScalaJacksonFormatter[T]]

    def +(serde: (StdSerializer[T], StdDeserializer[T]))(implicit ctag: ClassTag[T]): ScalaJacksonFormatter[T] =
        registerSerde(serde).asInstanceOf[ScalaJacksonFormatter[T]]
}

class ScalaJacksonRead[T: ClassTag] extends ScalaJacksonReader[T]

class ScalaJacksonWrite[T: ClassTag] extends ScalaJacksonWriter[T]

class ScalaJacksonFormat[T: ClassTag] extends ScalaJacksonFormatter[T] with ScalaJacksonReader[T] with ScalaJacksonWriter[T]

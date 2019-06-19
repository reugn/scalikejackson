package reug.scalikejackson

import java.security.InvalidParameterException
import java.text.DateFormat
import java.util.TimeZone

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
    self =>
    private[scalikejackson] val mappers: mutable.ArrayBuffer[ObjectMapper] = mutable.ArrayBuffer[ObjectMapper]()

    protected def registerSerializer[U: ClassTag](ser: StdSerializer[U]): this.type = {
        val clazz = implicitly[ClassTag[U]].runtimeClass.asInstanceOf[Class[U]]
        rawObjectMapper
            .registerModule((new SimpleModule).addSerializer(clazz, ser)) +=: mappers
        self
    }

    protected def registerDeserializer[U: ClassTag](de: StdDeserializer[U]): this.type = {
        val clazz = implicitly[ClassTag[U]].runtimeClass.asInstanceOf[Class[U]]
        rawObjectMapper
            .registerModule((new SimpleModule).addDeserializer(clazz, de)) +=: mappers
        self
    }

    protected def registerSerde[U: ClassTag](serde: (StdSerializer[U], StdDeserializer[U])): this.type = {
        val (ser, de) = serde
        val clazz = implicitly[ClassTag[U]].runtimeClass.asInstanceOf[Class[U]]
        rawObjectMapper
            .registerModule((new SimpleModule).addSerializer(clazz, ser).addDeserializer(clazz, de)) +=: mappers
        self
    }

    protected def parseConfiguration(config: Any*): ObjectMapper = {
        val mapper = rawObjectMapper
        config.toList.foreach {
            case s: PropertyNamingStrategy =>
                mapper.setPropertyNamingStrategy(s)
            case df: DateFormat =>
                mapper.setDateFormat(df)
            case tz: TimeZone =>
                mapper.setTimeZone(tz)
            case conf@_ =>
                throw new InvalidParameterException(s"Invalid configuration $conf")
        }
        mapper
    }

    protected def addMapper(mapper: ObjectMapper): Unit = mappers += mapper

    protected def rawObjectMapper: ObjectMapper = (new ObjectMapper() with ScalaObjectMapper)
        .registerModule(DefaultScalaModule)
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)

    protected def _serdeException(serde: String)(implicit ctag: ClassTag[T]): Nothing = {
        val cn = ctag.runtimeClass.getSimpleName
        throw new Exception(s"No $cn $serde found for json input")
    }
}

trait ScalaJacksonReader[T] extends ScalaJacksonParser[T] {
    private[scalikejackson] def read(str: String)(implicit ctag: ClassTag[T]): T = {
        val clazz = ctag.runtimeClass.asInstanceOf[Class[T]]
        for (i <- mappers.indices) {
            Try(mappers(i).readValue(str, clazz)).toOption.map(return _)
        }
        _serdeException(getClass.getSimpleName)
    }

    private[scalikejackson] def toJson(str: String)(implicit ctag: ClassTag[T]): JsonNode = {
        for (i <- mappers.indices) {
            Try(mappers(i).readTree(str)).toOption.map(return _)
        }
        _serdeException(getClass.getSimpleName)
    }

    private[scalikejackson] def convert(obj: Any)(implicit ctag: ClassTag[T]): T = {
        for (i <- mappers.indices) {
            Try(mappers(i).convertValue(obj, ctag.runtimeClass.asInstanceOf[Class[T]])).toOption.map(return _)
        }
        _serdeException(getClass.getSimpleName)
    }

    def or[U: ClassTag](de: StdDeserializer[U]): this.type =
        registerDeserializer(de)
}

trait ScalaJacksonWriter[T] extends ScalaJacksonParser[T] {
    private[scalikejackson] def write(obj: T)(implicit ctag: ClassTag[T]): String = {
        for (i <- mappers.indices) {
            Try(mappers(i).writeValueAsString(obj)).toOption.map(return _)
        }
        _serdeException(getClass.getSimpleName)
    }

    def or[U: ClassTag](ser: StdSerializer[U]): this.type =
        registerSerializer(ser)
}

trait ScalaJacksonFormatter[T] extends ScalaJacksonReader[T] with ScalaJacksonWriter[T] {
    def or[U: ClassTag](serde: (StdSerializer[U], StdDeserializer[U])): this.type =
        registerSerde(serde)
}

class ScalaJacksonRead[T: ClassTag](config: Any*) extends ScalaJacksonReader[T] {
    addMapper(parseConfiguration(config: _*))
}

class ScalaJacksonWrite[T: ClassTag](config: Any*) extends ScalaJacksonWriter[T] {
    addMapper(parseConfiguration(config: _*))
}

class ScalaJacksonFormat[T: ClassTag](config: Any*) extends ScalaJacksonFormatter[T] {
    addMapper(parseConfiguration(config: _*))
}

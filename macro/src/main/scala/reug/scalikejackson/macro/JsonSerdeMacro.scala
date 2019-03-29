package reug.scalikejackson.`macro`

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object JsonSerdeMacro {

    def reads[T: c.WeakTypeTag](c: blackbox.Context)(p: c.Tree*): c.universe.Tree = {
        import c.universe._
        val tpe = weakTypeOf[T]
        val inputs = p.toList

        def readPath(path: String): String = {
            splitPath(path).foldLeft("node")((p, k) => p + s""".get("$k")""")
        }

        val getters = inputs map {
            case q"$name[..$tparams]($path,$ftype)" =>
                val lookFor = readPath(path.toString())
                val getter = getterByType(ftype.toString())
                q"${
                    c.parse(if (isOptional(ftype.toString()))
                        s"Option($lookFor).map(_.$getter)"
                    else
                        s"$lookFor.$getter")
                }"
            case q"$name[..$tparams]($path,$ftype,$default)" =>
                val lookFor = readPath(path.toString())
                val getter = getterByType(ftype.toString())
                q"${
                    c.parse(s"Option($lookFor.$getter).getOrElse(${default.toString()})")
                }"
            case const@Literal(Constant(_)) =>
                const
            case invalid@_ =>
                c.abort(c.enclosingPosition, s"Invalid parameter $invalid")
        }
        val importJacksonCore = q"import com.fasterxml.jackson.core._"
        val importJacksonDatabind = q"import com.fasterxml.jackson.databind._"

        val result =
            q"""new com.fasterxml.jackson.databind.deser.std.StdDeserializer[$tpe](classOf[$tpe]) {
                $importJacksonCore
                $importJacksonDatabind
                override def deserialize(p: JsonParser, ctxt: DeserializationContext): $tpe = {
                    val node = p.getCodec.readTree[JsonNode](p)
                    new $tpe(..$getters)
                }
            }"""
        result
    }

    def writes[T: c.WeakTypeTag](c: blackbox.Context)(p: c.Tree): c.universe.Tree = {
        import c.universe._
        val tpe = weakTypeOf[T]
        val inputs = p match {
            case q"(..$params) => (..$expr)" =>
                expr
            case invalid@_ =>
                c.abort(c.enclosingPosition, s"Invalid writes input $invalid")
        }

        val writeAll = inputs map {
            case q"($path,$ptype,$pval)" =>
                (path.toString(), ptype.toString(), pval.toString())
            case invalid@_ =>
                c.abort(c.enclosingPosition, s"Invalid parameter $invalid")
        }
        val root = Root()
        writeAll foreach {
            in =>
                root.append(splitPath(in._1).toList, (in._2, in._3))
        }
        val gen = q"${c.parse(s"$generatorName")}"
        val writeObjects = q"${c.parse(s"${root.traverse(root)}")}"

        val importJacksonCore = q"import com.fasterxml.jackson.core._"
        val importJacksonDatabind = q"import com.fasterxml.jackson.databind._"

        val result =
            q"""new com.fasterxml.jackson.databind.ser.std.StdSerializer[$tpe](classOf[$tpe]) {
                $importJacksonCore
                $importJacksonDatabind
                override def serialize(value: $tpe, $gen: JsonGenerator,
                 provider: SerializerProvider): Unit = {
                    $writeObjects
                }
            }"""
        result
    }
}

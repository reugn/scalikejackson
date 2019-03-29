package reug.scalikejackson

package object `macro` {

    private[`macro`] final val generatorName = "gen"
    private[`macro`] final val valueName = "value"
    private[`macro`] final val startObject = s"$generatorName.writeStartObject()\n"
    private[`macro`] final val endObject = s"$generatorName.writeEndObject()\n"

    private[`macro`] def startObjectWithKey(key: String): String = {
        if (Root.ROOT_FIELD_NAME != key)
            s"""$generatorName.writeFieldName("$key")\n$startObject"""
        else
            startObject
    }

    private[`macro`] def prepareValue(v: String): String = {
        v.split("\\.").toList.updated(0, valueName).mkString(".")
    }

    private final val classOfRegex = ".*classOf\\[(.*)\\].*".r
    private final val PATH_DELIMITER = "/"

    private[`macro`] def getterByType(t: String): String = {
        t match {
            case classOfRegex("scala.Int") => "intValue"
            case classOfRegex("scala.Long") => "longValue"
            case classOfRegex("java.lang.String") => "asText"
            case classOfRegex("scala.Boolean") => "booleanValue"
            case classOfRegex(rCc) => s"as[$rCc]"
        }
    }

    private[`macro`] def writerByType(t: String): String = {
        t match {
            case classOfRegex("scala.Int") => "writeNumberField"
            case classOfRegex("scala.Long") => "writeNumberField"
            case classOfRegex("java.lang.String") => "writeStringField"
            case classOfRegex("scala.Boolean") => "writeBooleanField"
            case classOfRegex(_) => "writeObjectField"
        }
    }

    private[`macro`] def isOptional(clOf: String): Boolean = clOf.startsWith("scala.Option")

    private[`macro`] def splitPath(path: String): Seq[String] = {
        path.replace("\"", "").split(PATH_DELIMITER).map(_.trim)
    }

}

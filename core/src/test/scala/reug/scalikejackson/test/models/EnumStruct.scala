package reug.scalikejackson.test.models

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import play.api.libs.json.{Format, Json}
import reug.scalikejackson.commons.enums.MockEnum
import reug.scalikejackson.commons.enums.MockEnum.MockEnum
import reug.scalikejackson.{ScalaJacksonFormat, ScalaJacksonFormatter}

case class EnumStruct(
                         @JsonScalaEnumeration(classOf[EnumStructType]) enum: MockEnum
                     ) {

}

class EnumStructType extends TypeReference[MockEnum.type]

object EnumStruct {
    import reug.scalikejackson.commons.enums.MockEnumImplicit._
    implicit val formatter: ScalaJacksonFormatter[EnumStruct] = new ScalaJacksonFormat[EnumStruct]
    implicit val play_formatter: Format[EnumStruct] = Json.format[EnumStruct]
}
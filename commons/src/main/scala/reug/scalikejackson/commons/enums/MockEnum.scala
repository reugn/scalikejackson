package reug.scalikejackson.commons.enums

import play.api.libs.json.Writes
import reug.scalikejackson.commons.enums.MockEnum.MockEnum
import reug.scalikejackson.commons.utils.EnumUtils

object MockEnum extends Enumeration {
    type MockEnum = Value
    val OBJECT = Value("object")
    val ARRAY = Value("array")
}

object MockEnumImplicit {
    implicit val range_type_reader = EnumUtils.enumReads(MockEnum)
    implicit val range_type_writer: Writes[MockEnum] = EnumUtils.enumWrites
}

package reug.scalikejackson.benchmark.enums

import play.api.libs.json.Writes
import reug.scalikejackson.benchmark.enums.MockEnum.MockEnum
import reug.scalikejackson.benchmark.utils.EnumUtils

object MockEnum extends Enumeration {
    type MockEnum = Value
    val OBJECT = Value("object")
    val ARRAY = Value("array")
}

object MockEnumImplicit {
    implicit val range_type_reader = EnumUtils.enumReads(MockEnum)
    implicit val range_type_writer: Writes[MockEnum] = EnumUtils.enumWrites
}

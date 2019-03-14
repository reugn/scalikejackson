package reug.scalikejackson.test

import com.fasterxml.jackson.databind.node.ObjectNode
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsObject, Json}
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.commons.enums.MockEnum
import reug.scalikejackson.commons.utils.Resources
import reug.scalikejackson.play.{Json => LJson}
import reug.scalikejackson.test.models.{EnumStruct, MockStruct}
import reug.scalikejackson.utils.Converters._

class CompatibilityTest extends FlatSpec with Matchers with Resources {

    behavior of "Compatibility"

    val obj = MockStruct(1, "a", Some(true))
    val enum_obj = EnumStruct(MockEnum.OBJECT)

    it should "do linear search properly" in {
        val lite = (short_mixed_json.toJson \ "o" \ "ks").as[String]
        val play = (Json.parse(short_mixed_json) \ "o" \ "ks").as[String]

        lite shouldBe play
    }

    it should "do deep search properly" in {
        val lite = (short_mixed_json.toJson \\ "ks").head.as[String]
        val play = (Json.parse(short_mixed_json) \\ "ks").head.as[String]

        lite shouldBe play
    }

    it should "evaluate builders equally" in {
        val empty_bool: Option[Boolean] = None
        val none_lite = LJson.obj("i" -> 1, "b" -> empty_bool, "arr" -> LJson.arr(1, 2, 3))
        val none_play = Json.obj("i" -> 1, "b" -> empty_bool, "arr" -> Json.arr(1, 2, 3))

        LJson.stringify(LJson.arr()) shouldBe Json.stringify(Json.arr())
        LJson.stringify(LJson.obj()) shouldBe Json.stringify(Json.obj())
        LJson.stringify(LJson.arr(1, 2, 3)) shouldBe Json.stringify(Json.arr(1, 2, 3))
        LJson.stringify(LJson.obj(
            "i" -> 1, "b" -> Some(true), "arr" -> LJson.arr(1, 2, 3)
        )) shouldBe Json.stringify(Json.obj(
            "i" -> 1, "b" -> Some(true), "arr" -> Json.arr(1, 2, 3)
        ))
        LJson.stringify(none_lite.filter()) shouldBe Json.stringify(none_play - "b")
        LJson.stringify(none_lite) shouldBe Json.stringify(none_play)
        (none_lite \ "b").asOpt[Boolean] shouldBe (none_play \ "b").asOpt[Boolean]
        (none_lite \ "ba").asOpt[Boolean] shouldBe (none_play \ "ba").asOpt[Boolean]
    }

    it should "merge objects properly" in {
        val lite = (LJson.parse(short_custom_json).a[ObjectNode] ++ LJson.parse(short_custom_json_opt).a[ObjectNode]) - "c"
        val play = (Json.parse(short_custom_json).as[JsObject] ++ Json.parse(short_custom_json_opt).as[JsObject]) - "c"

        LJson.stringify(lite) shouldBe Json.stringify(play)
    }

    it should "parse enum objects properly" in {
        val enum_str = """{"enum":"object"}"""
        enum_obj.write shouldBe Json.stringify(Json.toJson(enum_obj))
        enum_str.read[EnumStruct] shouldBe Json.parse(enum_str).as[EnumStruct]
    }
}

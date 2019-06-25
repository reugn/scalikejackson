package reug.scalikejackson.test

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import org.scalatest.{FlatSpec, Matchers}
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.commons.utils.Resources
import reug.scalikejackson.play.Json
import reug.scalikejackson.test.models.MockStruct

class FunctionalityTest extends FlatSpec with Matchers with Resources {

    val obj = MockStruct(1, "a", Some(true))
    val obj_none = MockStruct(1, "a", None)

    behavior of "JacksonLite"

    it should "unmarshall json string properly to class" in {
        short_mixed_json.read[MockStruct] shouldBe obj
    }

    it should "unmarshall json string properly to JsonNode" in {
        short_mixed_json.toJson.at("/b").asBoolean() shouldBe true
    }

    it should "marshall to json properly" in {
        val expected = """{"in":1,"sn":"a","bn":true}"""
        obj.write shouldBe expected
    }

    it should "do custom read as expected" in {
        short_mixed_json.read[MockStruct] shouldBe obj
        short_mixed_json_opt.read[MockStruct] shouldBe obj_none
        short_custom_json.read[MockStruct] shouldBe obj
        short_custom_json_opt.read[MockStruct] shouldBe obj_none
    }

    it should "marshal sequence properly" in {
        val obj = Seq(MockStruct(1, "a", Some(true)), MockStruct(2, "b", Some(false)))
        val res = obj.write.toJson.asSeq[MockStruct]
        res.length shouldBe 2
    }

    it should "lookup keys properly" in {
        (short_custom_json.toJson \ "arr").asSeq[Int] shouldBe Seq(1, 2, 3)
        (short_custom_json.toJson \ "n" \ "m").asOpt[String] shouldBe None
        (short_custom_json.toJson \ "in").as[Int] shouldBe 1
    }

    it should "process json array as iterator" in {
        val stream = new ByteArrayInputStream(mock_json_array.getBytes(StandardCharsets.UTF_8))
        Json.iter[MockStruct](stream).toSeq.size shouldBe 2
    }

    it should "parse json as map properly" in {
        val v = """{"1":{"in":1,"sn":"a","bn":true},"2":{"i":1,"s":"a","b":true}}"""
        v.toJson.asMap[MockStruct] foreach {
            e => e._2 shouldBe obj
        }
    }

    it should "parse float value properly" in {
        Json.parse("7.877632165348009E31").as[Float] shouldBe 7.877632E31f
        Json.parse("1.199999988079071").as[Float] shouldBe 1.1999999f
        Json.parse("3.4028235677973366e38").as[Float] shouldBe 3.4028235E38f
        Json.parse("7.006492321624086e-46").as[Float] shouldBe 1.4E-45f
    }
}

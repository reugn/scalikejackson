package reug.scalikejackson.test

import org.scalatest.{FlatSpec, Matchers}
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.commons.models.CamelCaseClass
import reug.scalikejackson.commons.utils.Resources
import reug.scalikejackson.play.Json
import reug.scalikejackson.test.models.MockStruct

class CustomSerdeTest extends FlatSpec with Matchers with Resources {

    "compound object" should "parse properly" in {
        case class Container(
                                i_str: String,
                                i_mock: MockStruct
                            )

        val mock_instance = MockStruct(1, "a", Some(true))
        val container_instance = Container("asdf", mock_instance)

        val mock_writes = Json.writes[MockStruct](
            p => (
                ("in", classOf[Int], p.i),
                ("sn", classOf[String], p.s),
                ("bn", Option(classOf[Boolean]), p.b)
            )
        )

        val mock_reads = Json.reads[MockStruct](
            ("in", classOf[Int]),
            ("sn", classOf[String]),
            ("bn", Option(classOf[Boolean]))
        )

        implicit val mock_format = Json.format[MockStruct] + (mock_writes, mock_reads)
        implicit val container_format = Json.format[Container] + (mock_writes, mock_reads)

        mock_instance.write shouldBe """{"in":1,"sn":"a","bn":true}"""
        container_instance.write shouldBe """{"i_str":"asdf","i_mock":{"in":1,"sn":"a","bn":true}}"""
    }

    behavior of "Camel Case Property Naming"

    it should "parse properly" in {
        implicit val camel_case_format = Json.format[CamelCaseClass]
        camelCaseClassObj.write shouldBe camel_case_json
        Json.parse(camel_case_json).as[CamelCaseClass] shouldEqual camelCaseClassObj
    }
}

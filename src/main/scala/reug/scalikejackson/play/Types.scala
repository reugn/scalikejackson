package reug.scalikejackson.play

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}

/**
  * Maps Play Json types to Jackson/primitives
  */
object Types {

    type JsValue = JsonNode
    type JsObject = ObjectNode
    type JsArray = ArrayNode
    type JsString = String
    type JsBoolean = Boolean
}

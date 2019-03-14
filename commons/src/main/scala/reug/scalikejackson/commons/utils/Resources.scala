package reug.scalikejackson.commons.utils

trait Resources extends Utils {

    lazy val short_mixed_json: String = """{"i":1,"s":"a","b":true,"o":{"ks":"v","ki":2,"kb":false}}"""
    lazy val short_mixed_json_opt: String = """{"i":1,"s":"a","o":{"ks":"v","ki":2,"kb":false}}"""

    lazy val short_custom_json: String = """{"in":1,"sn":"a","bn":true,"c":3,"arr":[1,2,3]}"""
    lazy val short_custom_json_opt: String = """{"in":1,"sn":"a","c":3}"""

    lazy val mock_struct_json: String = """{"i":1,"s":"a","b":true}"""
    lazy val mock_json_array: String = """[{"i":1,"s":"a","b":true},{"i":2,"s":"b","b":false}]"""

    lazy val big_json: String = readResource("/json/big.json")
}

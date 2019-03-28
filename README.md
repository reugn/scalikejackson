## ScalikeJackson
[ ![Download](https://api.bintray.com/packages/reug/maven/scalikejackson/images/download.svg) ](https://bintray.com/reug/maven/scalikejackson/_latestVersion)

ScalikeJackson is a lightweight scala JSON library which provides [play-json](https://github.com/playframework/play-json) like interface and backed by [Jackson](https://github.com/FasterXML/jackson).  
A collection of benchmarks results can be viewed [here](https://github.com/reugn/scalikejackson/blob/master/benchmarks/src/main/scala/reug/scalikejackson/benchmark/README.md).

### Partial play-json features support

#### Basic operations
- [x] Basic reading and writing
- [x] Traversing a JSON object

#### Reading and writing objects
- [x] Automatic conversion (implicit reader/writer)
- [x] Reader/Writer DSL
- [x] Manual JSON construction

### Getting started
Add bintray resolver:
```sbtshell
resolvers += Resolver.bintrayRepo("reug", "maven")
```
Add ScalikeJackson as a dependency in your project:
```sbtshell
libraryDependencies += "reug" %% "scalikejackson" % "<version>"
```
### Usage
Basic operations:
```scala
import reug.scalikejackson.play.Json
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.utils.Converters._

val obj = Json.obj("i" -> 1,
    "b" -> false,
    "o" -> Json.obj("s" -> "str"),
    "arr" -> Json.arr(1, 2, 3))

(obj \ "o" \ "s").asOpt[String]
(obj \\ "s").head.as[String]
(obj \ "arr").asSeq[Int]

Json.stringify("""{"i":1}""".toJson)
```
Reading and writing objects:
```scala
import reug.scalikejackson.play.Json
import reug.scalikejackson.ScalaJacksonImpl._

case class MockStruct(
                         i: Int,
                         s: String,
                         b: Option[Boolean]
                     )
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

implicit val mock_format = Json.format[MockStruct]() or(mock_writes, mock_reads)
implicit val container_format = Json.format[Container]() or(mock_writes, mock_reads)

mock_instance.write shouldBe """{"in":1,"sn":"a","bn":true}"""
container_instance.write shouldBe """{"i_str":"asdf","i_mock":{"in":1,"sn":"a","bn":true}}"""
```
Support custom mapper configuration:
```scala
implicit val format = Json.format[MockStruct](PropertyNamingStrategy.SNAKE_CASE)
```
### Contributing
Feedback, bug reports, and pull requests are greatly appreciated.

### License
Licensed under the Apache 2.0 License.
See the [LICENSE](https://github.com/reugn/scalikejackson/blob/master/LICENSE.txt) file for details.

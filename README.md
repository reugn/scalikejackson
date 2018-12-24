## ScalikeJackson
[ ![Download](https://api.bintray.com/packages/reug/maven/scalikejackson/images/download.svg) ](https://bintray.com/reug/maven/scalikejackson/_latestVersion)

ScalikeJackson is a lightweight scala JSON library which provides [play-json](https://github.com/playframework/play-json) like interface and backed by [Jackson](https://github.com/FasterXML/jackson).

### Partial play-json features support

#### Basic operations
- [x] Basic reading and writing
- [x] Traversing a JSON object

#### Reading and writing objects
- [x] Automatic conversion (implicit reader/writer)
- [ ] Reader/Writer DSL
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
Basic usage:
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
A collection of benchmarks results can be viewed [here](https://github.com/reugn/scalikejackson/blob/master/benchmarks/src/main/scala/reug/scalikejackson/benchmark/README.md).

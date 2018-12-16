## ScalikeJackson

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
libraryDependencies += "reug" %% "scalikejackson" % "0.2.0"
```

A collection of benchmarks results can be viewed [here](https://github.com/reugn/scalikejackson/blob/master/benchmarks/src/main/scala/reug/scalikejackson/benchmark/README.md).


val jacksonVersion = "2.9.9"
val playJsonVersion = "2.7.4"
val playLib = "com.typesafe.play" %% "play-json" % playJsonVersion

lazy val commonSettings = Seq(
    organization := "reug",
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.11.12", scalaVersion.value, "2.13.0"),

    libraryDependencies ++= Seq(
        "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
        "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
        "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    scalacOptions := Seq(
        "-target:jvm-1.8",
        "-unchecked",
        "-deprecation",
        "-feature",
        "-encoding", "utf8",
        "-Xlint:-missing-interpolator"
    ),
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
)

lazy val noPublishSettings = Seq(
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    skip in publish := true
)

lazy val bench = (project in file("benchmarks")).settings(
    commonSettings,
    noPublishSettings
).enablePlugins(
    JmhPlugin
).settings(
    name := "scalikejackson-benchmarks",
    libraryDependencies += playLib
).dependsOn(core, commons)

lazy val commons = (project in file("commons")).settings(
    commonSettings,
    noPublishSettings
).settings(
    name := "scalikejackson-commons",
    libraryDependencies += playLib
)

lazy val `macro` = (project in file("macro")).settings(
    commonSettings
).settings(
    name := "scalikejackson-macro",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
)

lazy val core = (project in file("core")).settings(
    commonSettings
).settings(
    name := "scalikejackson",
    libraryDependencies += playLib % Test
).dependsOn(
    commons % "test->compile",
    `macro`
)

lazy val root = (project in file(".")).settings(
    noPublishSettings,
    name := "scalikejackson-root"
).aggregate(core, `macro`)
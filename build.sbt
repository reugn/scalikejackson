name := "scalikejackson"
organization := "reug"

version := "0.2.0"

scalaVersion := "2.12.8"
crossScalaVersions := Seq("2.11.12", scalaVersion.value)

enablePlugins(JmhPlugin)

val jacksonVersion = "2.9.7"

libraryDependencies ++= Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
    "com.typesafe.play" %% "play-json" % "2.6.10",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

unmanagedSourceDirectories in Compile += baseDirectory.value / "benchmarks" / "src" / "main" / "scala"

mappings in(Compile, packageBin) ~= {
    ms: Seq[(File, String)] =>
        ms filterNot {
            case (file, toPath) =>
                toPath contains "benchmark"
        }
}

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
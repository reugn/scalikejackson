package reug.scalikejackson.benchmark

import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.node.ObjectNode
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit}
import play.api.libs.json.{JsObject, Json}
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.benchmark.utils.Res
import reug.scalikejackson.play.{Json => LJson}

/**
  * sbt "bench/jmh:run -i 1 -wi 1 -f1 -t1 .*AlterBenchmark.*"
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class AlterBenchmark {

    @Benchmark
    def mutateBigLite(res: Res): Unit = {
        (LJson.parse(res.big_json).a[ObjectNode] ++ LJson.parse(res.short_mixed_json).a[ObjectNode]) - "r"
    }

    @Benchmark
    def mutateBigPlay(res: Res): Unit = {
        (Json.parse(res.big_json).as[JsObject] ++ Json.parse(res.short_mixed_json).as[JsObject]) - "r"
    }
}

package reug.scalikejackson.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit}
import play.api.libs.json.Json
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.benchmark.utils.Res
import reug.scalikejackson.test.models.MockStruct

/**
  * sbt
  * jmh:run -i 1 -wi 1 -f1 -t1 .*ReadBenchmark.*
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class ReadBenchmark {

    @Benchmark
    def unmarshalLite(res: Res): Unit = {
        for (_ <- 1 to 100) {
            res.short_custom_json_opt.read[MockStruct]
        }
    }

    @Benchmark
    def unmarshalPlay(res: Res): Unit = {
        for (_ <- 1 to 100) {
            Json.parse(res.short_custom_json_opt).as[MockStruct]
        }
    }

    @Benchmark
    def unmarshalLiteJson(res: Res): Unit = {
        for (_ <- 1 to 100) {
            res.short_custom_json_opt.toJson
        }
    }

    @Benchmark
    def unmarshalPlayJson(res: Res): Unit = {
        for (_ <- 1 to 100) {
            Json.parse(res.short_custom_json_opt)
        }
    }

    @Benchmark
    def unmarshalSeqLite(): Unit = {
        val obj = Seq(MockStruct(1, "a", Some(true)), MockStruct(2, "b", Some(false)))
        val str = obj.write
        for (_ <- 1 to 100) {
            str.toJson.asSeq[MockStruct]
        }
    }

    @Benchmark
    def unmarshalSeqPlay(): Unit = {
        val obj = Seq(MockStruct(1, "a", Some(true)), MockStruct(2, "b", Some(false)))
        val str = obj.write
        for (_ <- 1 to 100) {
            Json.parse(str).as[Seq[MockStruct]]
        }
    }
}

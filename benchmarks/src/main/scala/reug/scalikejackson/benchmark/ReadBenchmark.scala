package reug.scalikejackson.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit}
import org.openjdk.jmh.infra.Blackhole
import play.api.libs.json.Json
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.benchmark.utils.Res
import reug.scalikejackson.test.models.MockStruct

/**
  * sbt "bench/jmh:run -i 1 -wi 1 -f1 -t1 .*ReadBenchmark.*"
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class ReadBenchmark {

    @Benchmark
    def unmarshalLite(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume(res.short_custom_json_opt.read[MockStruct])
        }
    }

    @Benchmark
    def unmarshalPlay(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume(Json.parse(res.short_custom_json_opt).as[MockStruct])
        }
    }

    @Benchmark
    def unmarshalLiteJson(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume(res.short_custom_json_opt.toJson)
        }
    }

    @Benchmark
    def unmarshalPlayJson(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume(Json.parse(res.short_custom_json_opt))
        }
    }

    @Benchmark
    def unmarshalSeqLite(bh: Blackhole): Unit = {
        val obj = Seq(MockStruct(1, "a", Some(true)), MockStruct(2, "b", Some(false)))
        val str = obj.write
        for (_ <- 1 to 100) {
            bh.consume(str.toJson.asSeq[MockStruct])
        }
    }

    @Benchmark
    def unmarshalSeqPlay(bh: Blackhole): Unit = {
        val obj = Seq(MockStruct(1, "a", Some(true)), MockStruct(2, "b", Some(false)))
        val str = obj.write
        for (_ <- 1 to 100) {
            bh.consume(Json.parse(str).as[Seq[MockStruct]])
        }
    }
}

package reug.scalikejackson.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import play.api.libs.json.Json
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.benchmark.utils.Res

/**
  * sbt
  * jmh:run -i 1 -wi 1 -f1 -t1 .*SearchBenchmark.*
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class SearchBenchmark {

    @Benchmark
    def searchLinearLite(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume((res.short_mixed_json.toJson \ "o" \ "ki").as[Int])
        }
    }

    @Benchmark
    def searchLinearPlay(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume((Json.parse(res.short_mixed_json) \ "o" \ "ki").as[Int])
        }
    }

    @Benchmark
    def searchBigLinearLite(bh: Blackhole, res: Res): Unit = {
        bh.consume((res.big_json.toJson \ "a" \ "b" \ "c").as[Int])
    }

    @Benchmark
    def searchBigLinearPlay(bh: Blackhole, res: Res): Unit = {
        bh.consume((Json.parse(res.big_json) \ "a" \ "b" \ "c").as[Int])
    }

    @Benchmark
    def searchDeepLite(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume((res.short_mixed_json.toJson \\ "ks").head.as[String])
        }
    }

    @Benchmark
    def searchDeepPlay(bh: Blackhole, res: Res): Unit = {
        for (_ <- 1 to 100) {
            bh.consume((Json.parse(res.short_mixed_json) \\ "ks").head.as[String])
        }
    }
}

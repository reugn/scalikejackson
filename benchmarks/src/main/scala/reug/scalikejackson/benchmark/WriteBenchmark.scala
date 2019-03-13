package reug.scalikejackson.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import play.api.libs.json.Json
import reug.scalikejackson.ScalaJacksonImpl._
import reug.scalikejackson.test.models.MockStruct

/**
  * sbt
  * jmh:run -i 1 -wi 1 -f1 -t1 .*WriteBenchmark.*
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class WriteBenchmark {

    @Benchmark
    def marshalLite(bh: Blackhole): Unit = {
        val obj = MockStruct(1, "a", Some(true))
        for (_ <- 1 to 100) {
            bh.consume(obj.write)
        }
    }

    @Benchmark
    def marshalPlay(bh: Blackhole): Unit = {
        val obj = MockStruct(1, "a", Some(true))
        for (_ <- 1 to 100) {
            bh.consume(Json.toJson(obj).toString)
        }
    }
}

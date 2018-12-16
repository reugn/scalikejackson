package reug.scalikejackson.benchmark.utils

import java.io.InputStream

import scala.io.Source

trait Utils {

    protected def readResource(filename: String): String = {
        val stream: InputStream = getClass.getResourceAsStream(filename)
        Source.fromInputStream(stream).mkString
    }
}

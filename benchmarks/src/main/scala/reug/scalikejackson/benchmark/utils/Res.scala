package reug.scalikejackson.benchmark.utils

import org.openjdk.jmh.annotations.{Scope, State}

@State(Scope.Thread)
private[benchmark] class Res extends Resources
package reug.scalikejackson.benchmark.utils

import org.openjdk.jmh.annotations.{Scope, State}
import reug.scalikejackson.commons.utils.Resources

@State(Scope.Thread)
private[benchmark] class Res extends Resources
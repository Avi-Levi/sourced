package sourced.api

import sourced.stream.StreamKey

import scala.util.Try

trait SourcedOperations {
  def loadStream(key:StreamKey) : Try[StreamOperations]
  def registerStream(definition:StreamDefinition) : Unit
}

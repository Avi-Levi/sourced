package sourced.client.api

import scala.util.Try

trait SourcedOperations {
  def loadStream(key:StreamKey) : Try[StreamOperations]
}

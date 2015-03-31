package sourced.backend.stream

import sourced.handlers.api.StreamRef

object NoOpStream extends StreamRef{
  override def push(msg:AnyRef):Unit = {}
}

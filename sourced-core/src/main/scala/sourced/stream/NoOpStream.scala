package sourced.stream

import sourced.api.StreamRef

object NoOpStream extends StreamRef{
  override def push(msg:AnyRef):Unit = {}
}

package sourced.backend

import sourced.backend.stream.DefaultEventStream
import sourced.handlers.api.StreamRef

class DefaultStreamRef(private val stream:DefaultEventStream) extends StreamRef{
  override def push(msg: AnyRef): Unit = stream.pushSync(msg)
}

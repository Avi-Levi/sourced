package eventstore.stream

import eventstore.api.StreamRef

object NoOpStream extends StreamRef{
  override def push(msg:AnyRef):Unit = {}
}

package eventstore

import eventstore.api.StreamRef
import eventstore.stream.DefaultEventStream

class DefaultStreamRef(private val stream:DefaultEventStream) extends StreamRef{
  override def push(msg: AnyRef): Unit = stream.pushSync(msg)
}

package sourced.embedded

import sourced.backend.stream.DefaultEventStream
import sourced.client.api.StreamOperations

import scala.concurrent.Future
import scala.util.Try

class EmbeddedStreamOperations(stream:DefaultEventStream) extends StreamOperations{
  override def push(msgs: AnyRef*): Future[Try[Unit]] = stream.push(msgs :_*)
}

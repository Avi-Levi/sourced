package sourced.embedded

import sourced.api.StreamOperations
import sourced.stream.DefaultEventStream

import scala.concurrent.Future
import scala.util.Try

class EmbeddedStreamOperations(stream:DefaultEventStream) extends StreamOperations{
  override def push(msgs: AnyRef*): Future[Try[Unit]] = stream.push(msgs :_*)
}

package sourced.embedded

import akka.actor.ActorRef
import sourced.client.api.StreamOperations

import scala.concurrent.Future
import scala.util.Try

class EmbeddedStreamOperations(actor:ActorRef) extends StreamOperations{
  override def push(msgs: AnyRef*): Future[Try[Unit]] = ???
}

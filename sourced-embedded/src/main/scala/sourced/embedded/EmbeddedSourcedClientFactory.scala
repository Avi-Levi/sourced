package sourced.embedded

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import sourced.backend.api.EventsStorage
import sourced.backend.metadata.StreamMetadata
import sourced.backend.stream.EventStreamActor
import sourced.client.api.exceptions.MetadataNotFoundForStreamType
import sourced.client.api.{SourcedClientFactory, StreamClient, StreamKey}
import sourced.messages.PushEvents

import scala.collection._
import scala.concurrent.Future
import scala.util._

class EmbeddedSourcedClientFactory(val actorSystem: ActorSystem, val eventsStorage: EventsStorage, val streamTypeToMetadata : Map[String,StreamMetadata]) extends SourcedClientFactory{

  implicit val executionContext = actorSystem.dispatcher
  implicit val defaultTimeout = Timeout(100,TimeUnit.MILLISECONDS)

  class EmbeddedStreamClient(streamActorRef:ActorRef) extends StreamClient{
    override def push(msgs: AnyRef*): Future[Unit] = {
      val correlationKey = UUID.randomUUID().toString
      val f = streamActorRef ? PushEvents(correlationKey,msgs.toArray)
      f.map(_ => Unit)
    }
  }

  override def streamClient(key: StreamKey): Try[StreamClient] = {
    streamTypeToMetadata.get(key.streamType)
      .map{ metadata =>
      val actor = actorSystem.actorOf(Props(new EventStreamActor(key.id,metadata,eventsStorage)),name= s"streams-${key.streamType}-${key.id}")
      Success(new EmbeddedStreamClient(actor))
    }
      .getOrElse(Failure(new MetadataNotFoundForStreamType(key.streamType)))
  }
}


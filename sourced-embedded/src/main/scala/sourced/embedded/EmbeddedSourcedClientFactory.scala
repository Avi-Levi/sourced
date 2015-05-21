package sourced.embedded

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import sourced.backend.events.EventsStorage
import sourced.backend.metadata.StreamMetadata
import sourced.backend.stream.EventStreamActor
import sourced.client.api.exceptions.MetadataNotFoundForStreamType
import sourced.client.api.{SourcedClientFactory, StreamClient, StreamKey}
import sourced.messages.PushEvents

import scala.collection._
import scala.concurrent.Future
import scala.util._

class EmbeddedSourcedClientFactory(private val eventsStorage: EventsStorage, streamTypeToMetadata : Map[String,StreamMetadata]) extends SourcedClientFactory{

  val actorSystem = ActorSystem("embedded-sourced")

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
      val actor = actorSystem.actorOf(Props(new EventStreamActor(key.id,metadata,eventsStorage)),name= "stream-" + key.toString)
      Success(new EmbeddedStreamClient(actor))
    }
      .getOrElse(Failure(new MetadataNotFoundForStreamType(key.streamType)))
  }
}


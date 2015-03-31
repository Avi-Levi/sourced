package sourced.embedded

import sourced.backend.events.EventsStorage
import sourced.backend.exceptions.StreamDefinitionMissingException
import sourced.backend.metadata.StreamMetadata
import sourced.backend.stateLoader.DefaultStreamStateLoader
import sourced.backend.stream.DefaultEventStream
import sourced.client.api.{StreamKey, SourcedOperations, StreamOperations}

import scala.collection._
import scala.util.{Failure, Success, Try}

class EmbeddedSourcedOperations(private val eventsStorage: EventsStorage, streamTypeToMetadata : Map[String,StreamMetadata]) extends SourcedOperations{

  private val streamStateLoader = new DefaultStreamStateLoader(eventsStorage)

  override def loadStream(key: StreamKey): Try[StreamOperations] = {
    this.streamTypeToMetadata.get(key.streamType) match {
      case Some(metadata) =>
        val stream = new DefaultEventStream(key.id,streamStateLoader, metadata, eventsStorage)
        Success(new EmbeddedStreamOperations(stream))
      case None => Failure(new StreamDefinitionMissingException(key.streamType))
    }
  }
}
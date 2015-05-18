package sourced.embedded

import sourced.backend.events.EventsStorage
import sourced.backend.metadata.StreamMetadata
import sourced.backend.stateLoader.DefaultStreamStateLoader
import sourced.client.api.{SourcedOperations, StreamKey, StreamOperations}

import scala.collection._
import scala.util.Try

class EmbeddedSourcedOperations(private val eventsStorage: EventsStorage, streamTypeToMetadata : Map[String,StreamMetadata]) extends SourcedOperations{

  private val streamStateLoader = new DefaultStreamStateLoader(eventsStorage)

  override def loadStream(key: StreamKey): Try[StreamOperations] = ???
}
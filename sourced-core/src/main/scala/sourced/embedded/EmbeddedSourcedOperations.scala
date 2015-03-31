package sourced.embedded

import sourced.api._
import sourced.api.exceptions.{StreamDefinitionExistsException, StreamDefinitionMissingException}
import sourced.events.EventsStorage
import sourced.metadata.{DefaultHandlerMetadataBuilder, StreamMetadata}
import sourced.stateLoader.DefaultStreamStateLoader
import sourced.stream.{DefaultEventStream, StreamKey}

import scala.collection._
import scala.util.{Failure, Success, Try}

class EmbeddedSourcedOperations(private val eventsStorage: EventsStorage) extends SourcedOperations{

  private val handlerMetadataBuilder= new DefaultHandlerMetadataBuilder
  private val streamTypeToMetadata = mutable.Map[String,StreamMetadata]()
  private val streamStateLoader = new DefaultStreamStateLoader(eventsStorage)

  override def loadStream(key: StreamKey): Try[StreamOperations] = {
    this.streamTypeToMetadata.get(key.streamType) match {
      case Some(metadata) =>
        val stream = new DefaultEventStream(key,streamStateLoader, metadata, eventsStorage)
        Success(new EmbeddedStreamOperations(stream))
      case None => Failure(new StreamDefinitionMissingException(key.streamType))
    }
  }

  override def registerStream(definition:StreamDefinition) = {
    if(this.streamTypeToMetadata.contains(definition.streamType)){
      throw new StreamDefinitionExistsException(definition.streamType)
    }

    val handlersMetadata = this.handlerMetadataBuilder.forHandlerClasses(definition.handlers)

    this.streamTypeToMetadata.put(definition.streamType, StreamMetadata(definition.streamType,handlersMetadata))
  }
}
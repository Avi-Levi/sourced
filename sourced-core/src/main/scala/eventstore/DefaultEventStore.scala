package eventstore

import eventstore.api._
import eventstore.api.exceptions.{StreamDefinitionExistsException, StreamDefinitionMissingException}
import eventstore.events.EventsRepository
import eventstore.metadata.{HandlerMetadataBuilder, StreamMetadata}
import eventstore.stateLoader.StreamStateLoader
import eventstore.stream.{DefaultEventStream, StreamKey}

import scala.collection._
import scala.util.{Failure, Success, Try}

class DefaultEventStore(private val handlerMetadataBuilder: HandlerMetadataBuilder,
                        private val streamStateLoader:StreamStateLoader,
                        private val eventsRepository: EventsRepository) extends EventStore{

  private val streamTypeToMetadata = mutable.Map[String,StreamMetadata]()
  
  override def loadStream(key: StreamKey): Try[EventStream] = {
    this.streamTypeToMetadata.get(key.streamType) match {
      case Some(metadata) => Success(new DefaultEventStream(key,streamStateLoader, metadata, eventsRepository))
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
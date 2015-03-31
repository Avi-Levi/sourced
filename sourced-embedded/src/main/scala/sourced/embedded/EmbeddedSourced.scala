package sourced.embedded

import sourced.backend.events.EventsStorage
import sourced.backend.exceptions.StreamDefinitionExistsException
import sourced.backend.metadata.{HandlerMetadataBuilder, StreamMetadata}

import scala.collection.mutable

object EmbeddedSourced {

  private val streamTypeToMetadata = mutable.Map[String,StreamMetadata]()

  def getSourcedOperations()(implicit eventsStorage:EventsStorage) = new EmbeddedSourcedOperations(eventsStorage, streamTypeToMetadata)

  def registerStream(definition:StreamDefinition)(implicit handlerMetadataBuilder:HandlerMetadataBuilder) : Unit = {

    if(this.streamTypeToMetadata.contains(definition.streamType)){
      throw new StreamDefinitionExistsException(definition.streamType)
    }

    val handlersMetadata = handlerMetadataBuilder.forHandlerClasses(definition.handlers)

    this.streamTypeToMetadata.put(definition.streamType, StreamMetadata(definition.streamType,handlersMetadata))
  }
}


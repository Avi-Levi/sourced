package sourced.embedded

import sourced.backend.events.EventsStorage
import sourced.backend.exceptions.StreamDefinitionExistsException
import sourced.backend.metadata.{DefaultHandlerMetadataBuilder, StreamMetadata}

import scala.collection.mutable

object EmbeddedSourced {
  var eventsStorage : EventsStorage = null
  private val handlerMetadataBuilder= new DefaultHandlerMetadataBuilder
  private val streamTypeToMetadata = mutable.Map[String,StreamMetadata]()

  def getSourcedOperations = new EmbeddedSourcedOperations(eventsStorage, streamTypeToMetadata)

  def registerStream(definition:StreamDefinition) : Unit = {

    if(this.streamTypeToMetadata.contains(definition.streamType)){
      throw new StreamDefinitionExistsException(definition.streamType)
    }

    val handlersMetadata = this.handlerMetadataBuilder.forHandlerClasses(definition.handlers)

    this.streamTypeToMetadata.put(definition.streamType, StreamMetadata(definition.streamType,handlersMetadata))
  }
}

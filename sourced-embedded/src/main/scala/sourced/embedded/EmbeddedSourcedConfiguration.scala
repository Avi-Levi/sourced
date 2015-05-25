package sourced.embedded

import akka.actor.ActorSystem
import sourced.backend.api.EventsStorage
import sourced.backend.exceptions.StreamDefinitionExistsException
import sourced.backend.metadata.{HandlerMetadataBuilder, StreamMetadata}
import sourced.client.api.SourcedClientFactory

import scala.collection.mutable

class EmbeddedSourcedConfiguration(eventsStorage:EventsStorage)(implicit actorSystem:ActorSystem) {

  private val streamTypeToMetadata = mutable.Map[String,StreamMetadata]()

  def newClientFactory() : SourcedClientFactory = new EmbeddedSourcedClientFactory(actorSystem, eventsStorage, streamTypeToMetadata)

  def registerStream(definition:StreamDefinition)(implicit handlerMetadataBuilder:HandlerMetadataBuilder) : Unit = {

    if(this.streamTypeToMetadata.contains(definition.streamType)){
      throw new StreamDefinitionExistsException(definition.streamType)
    }

    val handlersMetadata = handlerMetadataBuilder.forHandlerClasses(definition.handlers)

    this.streamTypeToMetadata.put(definition.streamType, StreamMetadata(definition.streamType,handlersMetadata))
  }
}


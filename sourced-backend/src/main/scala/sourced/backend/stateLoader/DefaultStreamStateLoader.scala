package sourced.backend.stateLoader

import sourced.backend.events.{EventObject, EventsStorage}
import sourced.backend.metadata.{HandlerMetadata, StreamMetadata}
import sourced.backend.{DefaultEventDispatcher, TopicsToHandlersIndex}
import sourced.handlers.api.EventsHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class DefaultStreamStateLoader(private val eventsRepository:EventsStorage)
  extends StreamStateLoader {
     override def loadStreamState(streamId:String, streamMetadata: StreamMetadata) : Future[LoadStateResponse] = {
       val p = promise[LoadStateResponse]()
       val handlersIndex = new TopicsToHandlersIndex(streamMetadata.handlersMetadata)

       def handleEventLoaded(e: EventObject) = {
         val eventMetadata = streamMetadata.getEventMetadata(e.body.getClass)
         handlersIndex.dispatch(e.body,eventMetadata)
       }

       this.eventsRepository.iterate(streamId, handleEventLoaded) onSuccess {
         case lastEventIndex => p.success(LoadStateResponse(lastEventIndex,handlersIndex))
       }

       p.future
     }
     private def createDispatchersForHandler(handlerInstance:EventsHandler, handlerMetadata:HandlerMetadata) = {
       handlerMetadata.topicToMethodsMap.flatMap{x=>
         val (topic,methods) = x
         methods.map(m=>new DefaultEventDispatcher(topic,m,handlerInstance))
       }
     }
   }

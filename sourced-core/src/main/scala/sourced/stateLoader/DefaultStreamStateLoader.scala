package sourced.stateLoader

import sourced.api.HandlersInstanceBuilder
import sourced.events.{EventsStorage, EventObject}
import sourced.DefaultEventDispatcher
import sourced.metadata.{StreamMetadata, HandlerMetadata}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class DefaultStreamStateLoader(private val eventsRepository:EventsStorage)
  extends StreamStateLoader with HandlersInstanceBuilder {

     // will support loading persistent serialized handlers, so method is async because of expected io
     override def loadStreamState(streamId:String, streamMetadata: StreamMetadata) : Future[LoadStateResponse] = {
       val handlersMetadataToInstanceMap = streamMetadata.handlersMetadata.map(m=>(m,this.createHandlerInstance(m.handlerClass)))

       replayEvents(streamId, handlersMetadataToInstanceMap, streamMetadata)
     }
     private def replayEvents(streamId:String,handlers:Iterable[(HandlerMetadata,AnyRef)],streamMetadata: StreamMetadata) : Future[LoadStateResponse] = {
       val p = promise[LoadStateResponse]

       val dispatchers = handlers.flatMap(x=>this.createDispatchersForHandler(x._2,x._1))
       val topicToDispatchersMap = dispatchers.groupBy(_.topic)

       def handleEventLoaded(e: EventObject) = {
         val eventMetadata = streamMetadata.getEventMetadata(e.body.getClass)

         eventMetadata.topics.foreach{t =>
           topicToDispatchersMap.get(t).map(dispatchers => dispatchers.foreach(_.dispatch(e.body)))
         }
       }

       this.eventsRepository.iterate(streamId, handleEventLoaded) onSuccess {
         case lastEventIndex => p.success(LoadStateResponse(lastEventIndex,topicToDispatchersMap,handlers))
       }

       p.future
     }
     private def createDispatchersForHandler(handlerInstance:AnyRef, handlerMetadata:HandlerMetadata) = {
       handlerMetadata.topicToMethodsMap.flatMap{x=>
         x._2.map(m=>new DefaultEventDispatcher(x._1,m,handlerInstance))
       }
     }
   }

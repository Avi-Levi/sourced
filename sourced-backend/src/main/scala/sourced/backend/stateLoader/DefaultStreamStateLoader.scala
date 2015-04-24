package sourced.backend.stateLoader

import sourced.backend.HandlersFactory
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.events.{EventObject, EventsStorage}
import sourced.backend.metadata.StreamMetadata

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class DefaultStreamStateLoader(private val eventsRepository:EventsStorage)
  extends StreamStateLoader with HandlersFactory{
     override def loadStreamState(streamId:String, streamMetadata: StreamMetadata) : Future[LoadStateResponse] = {
       val p = promise[LoadStateResponse]()
       val handlersInfo = streamMetadata.handlersMetadata.map(hm => HandlerInfo(hm.topicToMethodsMap,()=>createHandlerInstance(hm.handlerClass)))
       lazy val handlersIndex = new TopicsToStreamHandlersIndex(handlersInfo,streamMetadata.getEventMetadata)

       def handleEventLoaded(e: EventObject) = {
         handlersIndex.dispatch(e.body)
       }

       this.eventsRepository.iterate(streamId, handleEventLoaded) onSuccess {
         case lastEventIndex => p.success(LoadStateResponse(lastEventIndex,handlersIndex))
       }

       p.future
     }
   }

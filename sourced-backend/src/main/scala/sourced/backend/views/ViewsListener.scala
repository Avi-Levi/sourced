package sourced.backend.views

import sourced.backend.HandlersFactory
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.events.EventObject
import sourced.backend.metadata.{EventMetadata, StreamMetadata}

import scala.concurrent.Future

class ViewsListener(viewsMetadata:Iterable[ViewMetadata], streamMetadata: StreamMetadata) extends HandlersFactory{
  val createTopicToViewClass = viewsMetadata.map{vm=>(vm.createTopic,vm)}.toMap
  def onEventsCommitted(newEvents:Iterable[EventObject]) : Future[Unit] = {

    val viewsToCreate = newEvents
      .flatMap(e=>streamMetadata.getEventMetadata(e.body.getClass).topics)
      .flatMap(createTopicToViewClass.get)
      .toArray

    if(viewsToCreate.nonEmpty){
      val handlersInfo = viewsMetadata.map(vm => HandlerInfo(vm.topicToMethodsMap,()=>createHandlerInstance(vm.viewHandlerClass)))
      val handlersIndex = new TopicsToStreamHandlersIndex(handlersInfo,streamMetadata.getEventMetadata)
      newEvents.foreach(handlersIndex.dispatch)
      handlersIndex.getUpdatedHandlers
    }

    val eventsWithMetadata = newEvents.map(e=>(e,streamMetadata.getEventMetadata(e.body.getClass)))
    val eventsThatCreateViews = eventsWithMetadata.filter(x=>isEventCreateView(x._2))
    /*eventsThatCreateViews.map()*/

    Future.successful()
  }
  private def isEventCreateView(e:EventMetadata) : Boolean = ???
}

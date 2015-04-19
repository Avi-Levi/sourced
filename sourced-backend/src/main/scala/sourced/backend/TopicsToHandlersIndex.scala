package sourced.backend

import sourced.backend.metadata.{EventMetadata, HandlerMetadata}
import sourced.handlers.api.EventsHandler

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class TopicsToHandlersIndex(handlersMetadata:Iterable[HandlerMetadata]) extends HandlersInstanceBuilder{

  this.loadHandlers(handlersMetadata)

  lazy val topicToDispatchersMap = mutable.Map[String, ListBuffer[EventDispatcher]]()
  lazy val handlersInstances = ListBuffer[EventsHandler]()

  def dispatch(event:AnyRef, eventMetadata:EventMetadata) = eventMetadata.topics.foreach{t =>
    topicToDispatchersMap.get(t).foreach(dispatchers => dispatchers.foreach(_.dispatch(event)))
  }

  private def loadHandlers(handlersMetadata:Iterable[HandlerMetadata]) = {
    val handlersMetadataToInstanceTupels = handlersMetadata.map(md=>(md,this.createHandlerInstance(md.handlerClass)))
    handlersInstances ++= handlersMetadataToInstanceTupels.map(_._2)

    handlersMetadataToInstanceTupels
      .flatMap(x=>this.createDispatchersForHandler(x._2,x._1))
      .groupBy(_.topic)
      .foreach{x=>
        val (topic,dispatchers) = x
        this.topicToDispatchersMap.getOrElseUpdate(x._1, new ListBuffer[EventDispatcher]()) ++= x._2
      }
  }
  private def createDispatchersForHandler(handlerInstance:EventsHandler, handlerMetadata:HandlerMetadata) = {
    handlerMetadata.topicToMethodsMap.flatMap{x=>
      val (topic,methods) = x
      methods.map(m=>new DefaultEventDispatcher(topic,m,handlerInstance))
    }
  }
}
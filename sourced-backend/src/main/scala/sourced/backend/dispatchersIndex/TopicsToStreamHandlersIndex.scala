package sourced.backend.dispatchersIndex

import java.lang.reflect.Method

import sourced.backend.metadata.EventMetadata

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class TopicsToStreamHandlersIndex(handlersInfo:Iterable[HandlerInfo], getEventMetadata:Class[_] => EventMetadata) extends TopicsToHandlersIndex{
  class EventDispatcher(val topic:String,val method:Method, val handlerInfo:HandlerInfo){
    def dispatch(msg: AnyRef) = {
      method.invoke(handlerInfo.instance, msg)
      handlerInfo.setUpdated
    }
  }

  this.loadHandlers(handlersInfo)
  
  lazy val topicToDispatchersMap = mutable.Map[String, ListBuffer[EventDispatcher]]()
  lazy val handlersInstances = ListBuffer[HandlerInfo]()

  override def dispatch(event:AnyRef) = {
    getEventMetadata(event.getClass).topics.foreach{t =>
    topicToDispatchersMap.get(t).foreach(dispatchers => dispatchers.foreach(_.dispatch(event)))}
  }

  override def forEachInstance(f: (AnyRef) => Unit): Unit = this.handlersInstances.map(_.instance).foreach(f)

  override def getUpdatedHandlers : Iterable[AnyRef] = this.handlersInstances.filter(_.isUpdated).map(_.instance)

  private def loadHandlers(handlersInfo:Iterable[HandlerInfo]) = {
    handlersInstances ++= handlersInfo

    handlersInfo
      .flatMap(createDispatchersForHandler)
      .groupBy(_.topic)
      .foreach{x=> val (topic,dispatchers) = x
        this.topicToDispatchersMap.getOrElseUpdate(x._1, new ListBuffer[EventDispatcher]()) ++= x._2
      }
  }
  private def createDispatchersForHandler(handlerInfo:HandlerInfo) = {
    handlerInfo.topicToMethodsMap.flatMap{x=>
      val (topic,methods) = x
      methods.map(m=>new EventDispatcher(topic,m,handlerInfo))
    }
  }
}
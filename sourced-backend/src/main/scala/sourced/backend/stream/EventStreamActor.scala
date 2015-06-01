package sourced.backend.stream

import akka.actor.{Actor, ActorRef}
import sourced.backend.HandlersFactory
import sourced.backend.api.{EventObject, EventsStorage}
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToHandlersIndex, TopicsToStreamHandlersIndex}
import sourced.backend.metadata.StreamMetadata
import sourced.handlers.api.{EventsHandler, StreamRef}
import sourced.messages._

import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.util.{Failure, Success}

class EventStreamActor(private val streamId: String,
                       private val streamMetadata: StreamMetadata,
                       private val eventsStorage: EventsStorage) extends Actor with HandlersFactory{

  class StreamRefImpl() extends StreamRef{
    override def push(msg: AnyRef): Unit = pushSingle(msg)
  }

  import context.dispatcher
  
  var handlersIndex : TopicsToHandlersIndex = null
  val loadStateFuture = loadState
  var currentEventIndex = 0L
  lazy val newEvents = ListBuffer[EventObject]()

  override def receive: Receive = {
    case msg:PushEvents => handlePushEvents(msg)
    case msg:PushNewEventsWithSender => handlePushNewEvents(msg.original, msg.sender)
  }

  private def handlePushEvents(msg: PushEvents): Unit = {
    if (loadStateFuture.isCompleted) {
      handlePushNewEvents(msg, sender())
    } else {
      val originalSender = sender()
      loadStateFuture.onComplete {
        case Success(_) => self ! PushNewEventsWithSender(msg, originalSender)
        case Failure(t) => throw t
      }
    }
  }

  private def handlePushNewEvents(msg:PushEvents, sender:ActorRef)={
    this.push(msg.events)

    if (this.newEvents.nonEmpty) {

      val saveFuture = this.eventsStorage.save(this.streamId, this.newEvents)

      saveFuture onComplete{
        case Success(_) =>
          newEvents.clear()
          sender ! PushSuccess(msg.requestCorrelationKey)
        case Failure(t) =>
          sender ! PushFailure(msg.requestCorrelationKey)
      }
    } else {
      sender ! PushSuccess(msg.requestCorrelationKey)
    }
  }

  private def loadState : Future[Unit] = {
    val handlersInfo = streamMetadata.handlersMetadata.map(hm => HandlerInfo(hm.topicToMethodsMap,()=>createHandlerInstance(hm.handlerClass)))
    lazy val handlersIndex = new TopicsToStreamHandlersIndex(handlersInfo,streamMetadata.getEventMetadata)

    def handleEventLoaded(e: EventObject) = {
      handlersIndex.dispatch(e.body)
    }

    eventsStorage.iterate(streamId, handleEventLoaded).map(lastEventIndex => handleStateLoaded(lastEventIndex,handlersIndex))
  }

  private def handleStateLoaded(lastEventIndex:Long,handlersIndex: TopicsToHandlersIndex): Unit = {
    this.injectStreamRef(handlersIndex)
    this.handlersIndex = handlersIndex
  }

  private def injectStreamRef(handlersIndex: TopicsToHandlersIndex) = {
    val streamRef = new StreamRefImpl()
    handlersIndex.forEachInstance(_.asInstanceOf[EventsHandler].setStreamRef(streamRef))
  }

  private def nextEventIndex = {
    this.currentEventIndex += 1
    this.currentEventIndex
  }

  private def push(events: Array[AnyRef]) = {
    events.foreach(this.pushSingle)
  }

  private def pushSingle(msg: AnyRef): Unit = {
    val eventObj = EventObject(this.nextEventIndex, msg.getClass.getName, msg)

    this.newEvents += eventObj

    this.handlersIndex.dispatch(msg)
  }
}

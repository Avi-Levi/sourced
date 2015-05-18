package sourced.backend.stream

import akka.actor.{ActorRef, Actor}
import sourced.backend.dispatchersIndex.TopicsToHandlersIndex
import sourced.backend.events.{EventObject, EventsStorage}
import sourced.backend.metadata.StreamMetadata
import sourced.backend.stateLoader.{LoadStateResponse, StreamStateLoader}
import sourced.handlers.api.{EventsHandler, StreamRef}
import sourced.messages.{PushFailure, PushSuccess, PushEvents}

import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.util.{Failure, Success}

class EventStreamActor(private val streamId: String,
                       private val stateLoader:StreamStateLoader,
                       private val streamMetadata: StreamMetadata,
                       private val eventsStorage: EventsStorage) extends Actor{

  class StreamRefImpl() extends StreamRef{
    override def push(msg: AnyRef): Unit = pushSingle(msg)
  }

  import context.dispatcher
  
  var handlersIndex : TopicsToHandlersIndex = null
  val loadStateFuture = loadState
  var currentEventIndex = 0L
  lazy val newEvents = ListBuffer[EventObject]()

  override def receive: Receive = {
    case msg:PushEvents =>
      if(loadStateFuture.isCompleted){
        handlePushNewEvents(msg, sender())
      }else{
        loadStateFuture.onSuccess{case _ => self ! PushNewEventsWithSender(msg,sender()) }
      }
    case msg:PushNewEventsWithSender => handlePushNewEvents(msg.original, msg.sender)
  }

  private def handlePushNewEvents(msg:PushEvents, sender:ActorRef)={
    this.push(msg.events)

    if (!this.newEvents.isEmpty) {

      val saveFuture = this.eventsStorage.save(this.streamId, this.newEvents)

      saveFuture onComplete{
        case Success(_) =>
          newEvents.clear
          sender ! PushSuccess(msg.requestCorrelationKey)
        case Failure(t) =>
          sender ! PushFailure(msg.requestCorrelationKey)
      }
    } else {
      sender ! PushSuccess(msg.requestCorrelationKey)
    }
  }

  private def loadState : Future[Unit] = {
    import context.dispatcher

    this.stateLoader.loadStreamState(this.streamId, this.streamMetadata).map(handleStateLoaded)
  }

  private def handleStateLoaded(response: LoadStateResponse): Unit = {
    this.injectStreamRef(response.handlersIndex)
    this.handlersIndex = response.handlersIndex
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

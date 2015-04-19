package sourced.backend.stream

import sourced.backend._
import sourced.backend.events.{EventObject, EventsStorage}
import sourced.backend.metadata.StreamMetadata
import sourced.backend.stateLoader.{LoadStateResponse, StreamStateLoader}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{Failure, Success, Try}

class DefaultEventStream(private val streamId: String,
                         private val stateLoader:StreamStateLoader,
                         private val streamMetadata: StreamMetadata,
                         private val eventsStorage: EventsStorage)
  extends Logging{

  private var handlersCollection : TopicsToHandlersIndex = null
  private lazy val newEvents = ListBuffer[EventObject]()
  
  private val loadStateFuture = loadState
  private var currentEventIndex = 0L
  
  def push(events: AnyRef*): Future[Try[Unit]] = {
    val p = promise[Try[Unit]]()

    def onStateLoaded(s: Try[Unit] with Success[Any]) = {
      events.foreach(this.pushSync)

      if (!this.newEvents.isEmpty) {
        val saveFuture = this.eventsStorage.save(this.streamId, this.newEvents)
        saveFuture onSuccess {
          case _ => newEvents.clear
        }
        p.completeWith(saveFuture)
      } else {
        p.success(s)
      }
    }

    loadStateFuture onComplete {
      case s:Success[_] => onStateLoaded(s)
      case f:Failure[Unit] => p.failure(f.exception)
    }

    p.future
  }

  private[sourced] def pushSync(msg: AnyRef): Unit = {
    val eventObj = EventObject(this.nextEventIndex, msg.getClass.getName, msg)

    this.newEvents += eventObj

    val eventMetadata = this.streamMetadata.getEventMetadata(msg.getClass)

    this.handlersCollection.dispatch(msg,eventMetadata)
  }

  private def loadState : Future[Unit] = {
    val p = promise[Unit]()
    
    this.stateLoader.loadStreamState(this.streamId, this.streamMetadata) andThen{
      case Success(response) => 
        handleStateLoaded(response)
        p.success()
      case Failure(t) =>
        p.failure(t)
    }
    
    p.future
  }

  private def handleStateLoaded(response: LoadStateResponse): Unit = {
    this.injectStreamRef(response.handlersIndex)
    this.handlersCollection = response.handlersIndex
  }

  private def nextEventIndex = {
    this.currentEventIndex += 1
    this.currentEventIndex
  }

  private def injectStreamRef(handlersCollection: TopicsToHandlersIndex) = {
    val streamRef = new DefaultStreamRef(this)
    handlersCollection.handlersInstances.foreach{_.setStreamRef(streamRef)}
  }
}
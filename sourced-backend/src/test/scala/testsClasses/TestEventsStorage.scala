package testsClasses

import sourced.backend.events.{EventObject, EventsStorage}

import scala.concurrent.{Future, _}
import scala.util.Success

class TestEventsStorage(events:List[EventObject]) extends EventsStorage{
  
  var newEvents: Array[EventObject] = null
  override def iterate(streamId: String, handleEvent: (EventObject) => Unit): Future[Long] = {
    events.foreach(handleEvent)
    promise[Long]().complete(Success(events.size)).future
  }

  def save(streamId:String, events:Iterable[EventObject]) : Future[Unit] = {
    this.newEvents = events.toArray
    Future.successful()
  }
}

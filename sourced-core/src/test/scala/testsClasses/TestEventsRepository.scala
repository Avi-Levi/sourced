package testsClasses

import sourced.events._

import scala.concurrent.{Future, _}
import scala.util.{Try, Success}

class TestEventsRepository(events:List[EventObject]) extends EventsStorage{
  
  var newEvents: Array[EventObject] = null
  override def iterate(streamId: String, handleEvent: (EventObject) => Unit): Future[Long] = {
    events.foreach(handleEvent)
    promise[Long]().complete(Success(events.size)).future
  }

  override def save(streamId:String, events: Iterable[EventObject]): Future[Try[Unit]] = {
    this.newEvents = events.toArray
    Future.successful(Success())
  }
}

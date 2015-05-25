package sourced.testing.utils

import sourced.backend.api.{EventObject, EventsStorage}

import scala.concurrent.{Future, _}

class TestEventsStorage(events:List[EventObject], waitTime:Long = 0)(implicit val executionContext: ExecutionContext) extends EventsStorage{

  var newEvents: Array[EventObject] = null

  override def iterate(streamId: String, handleEvent: (EventObject) => Unit): Future[Long] = {
    val res: Future[Long] = if(waitTime == 0){
      Future.successful(doIterate(handleEvent))
    }else{
      future[Long]{
        Thread.sleep(waitTime)
        doIterate(handleEvent)
      }
    }

    res
  }

  def doIterate(handleEvent: (EventObject) => Unit) = {
    events.foreach(handleEvent)
    events.size
  }
  def save(streamId:String, events:Iterable[EventObject]) : Future[Unit] = {
    this.newEvents = events.toArray
    Future.successful()
  }
}

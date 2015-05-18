package sourced.backend.events

import scala.concurrent.Future

trait EventsStorage{
  def iterate(streamId:String, handleEvent:EventObject => Unit) : Future[Long]
  def save(streamId:String, events:Iterable[EventObject]) : Future[Unit]
}

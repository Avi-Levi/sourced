package sourced.backend.events

import scala.concurrent.Future
import scala.util.Try

trait EventsStorage{
  def iterate(streamId:String, handleEvent:EventObject => Unit) : Future[Long]
  def save(streamId:String, events:Iterable[EventObject]) : Future[Try[Unit]]
}

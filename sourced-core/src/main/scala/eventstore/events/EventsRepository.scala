package eventstore.events

import scala.concurrent.Future
import scala.util.Try

trait EventsRepository {
  def iterate(streamId:String, handleEvent:EventObject => Unit) : Future[Long]
  def save(events:Iterable[EventObject]) : Future[Try[Unit]]
}

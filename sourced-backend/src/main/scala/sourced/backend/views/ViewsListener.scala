package sourced.backend.views

import sourced.backend.events.EventObject

import scala.concurrent.Future

class ViewsListener {
  def onEventsCommitted(newEvents:Iterable[EventObject]) : Future[Unit] = ???
}

package sourced.backend.views

import sourced.backend.HandlersInstanceBuilder
import sourced.backend.events.EventObject
import sourced.backend.metadata.{EventMetadata, StreamMetadata}

import scala.concurrent.Future

class ViewsListener(viewCofiguration:ViewsConfiguration, streamMetadata: StreamMetadata) extends HandlersInstanceBuilder{
  def onEventsCommitted(newEvents:Iterable[EventObject]) : Future[Unit] = {
    val eventsWithMetadata = newEvents.map(e=>(e,streamMetadata.getEventMetadata(e.body.getClass)))
    val eventsThatCreateViews = eventsWithMetadata.filter(x=>isEventCreateView(x._2))
    /*eventsThatCreateViews.map()*/

    Future.successful()
  }
  private def isEventCreateView(e:EventMetadata) : Boolean = ???
}

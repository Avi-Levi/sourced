package sourced.mongodb.views

import com.sun.corba.se.impl.orbutil.closure.Future
import sourced.backend.events.EventObject

trait EventsCommittedListener {
  def committed(streamId:String, streamType:String, events:Array[EventObject]):Future
}

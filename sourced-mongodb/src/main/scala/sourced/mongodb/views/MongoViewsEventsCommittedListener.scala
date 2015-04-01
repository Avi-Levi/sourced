package sourced.mongodb.views

import sourced.backend.events.EventObject
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}
import scala.collection

class MongoViewsEventsCommittedListener extends EventsCommittedListener {
  val topicToQueryGeneratorIndex = mutable.Map[String, AnyRef => String]
  override def committed(streamId:String, streamType:String, events:Array[EventObject]):Future = {
    val p = promise()

  }
}

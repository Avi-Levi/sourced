package sourced.mongodb.eventsStorage

case class PersistedEventObject(index:Long, className:String, body:String){}

package eventstore.events

case class EventObject(index:Long, className:String, body:AnyRef) {}

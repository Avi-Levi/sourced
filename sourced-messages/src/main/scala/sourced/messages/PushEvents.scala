package sourced.messages

case class PushEvents(requestCorrelationKey:String, events:Array[AnyRef])
package sourced.messages

case class PushEvents(streamId:String, requestCorrelationKey:String, events:Array[AnyRef])
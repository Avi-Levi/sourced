package sourced.api

case class StreamDefinition(streamType:String,handlers: Iterable[Class[_]]) {}

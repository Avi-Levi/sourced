package sourced.embedded

case class StreamDefinition(streamType:String,handlers: Iterable[Class[_]]) {}

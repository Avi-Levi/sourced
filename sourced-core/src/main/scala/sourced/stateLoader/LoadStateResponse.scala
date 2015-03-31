package sourced.stateLoader

import sourced.EventDispatcher
import sourced.metadata.HandlerMetadata

case class LoadStateResponse(lastEventIndex:Long,eventDispatchers:Map[String,Iterable[EventDispatcher]], handlers:Iterable[(HandlerMetadata,AnyRef)]) {}
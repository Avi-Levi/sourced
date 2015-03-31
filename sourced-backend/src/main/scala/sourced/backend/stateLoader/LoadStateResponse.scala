package sourced.backend.stateLoader

import sourced.backend.EventDispatcher
import sourced.backend.metadata.HandlerMetadata

case class LoadStateResponse(lastEventIndex:Long,eventDispatchers:Map[String,Iterable[EventDispatcher]], handlers:Iterable[(HandlerMetadata,AnyRef)]) {}
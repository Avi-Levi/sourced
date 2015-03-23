package eventstore.stateLoader

import eventstore.EventDispatcher
import eventstore.metadata.HandlerMetadata

case class LoadStateResponse(lastEventIndex:Long,eventDispatchers:Map[String,Iterable[EventDispatcher]], handlers:Iterable[(HandlerMetadata,AnyRef)]) {}
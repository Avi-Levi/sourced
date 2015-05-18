package sourced.backend.stream

import akka.actor.ActorRef
import sourced.messages.PushEvents

case class PushNewEventsWithSender(original:PushEvents, sender:ActorRef)
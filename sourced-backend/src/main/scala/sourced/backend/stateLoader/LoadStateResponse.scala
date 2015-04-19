package sourced.backend.stateLoader

import sourced.backend.TopicsToHandlersIndex

case class LoadStateResponse(lastEventIndex:Long,handlersIndex: TopicsToHandlersIndex) {}
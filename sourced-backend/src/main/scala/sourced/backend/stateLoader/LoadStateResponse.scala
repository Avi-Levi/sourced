package sourced.backend.stateLoader

import sourced.backend.dispatchersIndex.TopicsToHandlersIndex

case class LoadStateResponse(lastEventIndex:Long,handlersIndex: TopicsToHandlersIndex) {}
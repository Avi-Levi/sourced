package sourced.embedded

import sourced.events.EventsStorage

object EmbeddedSourced {
  var eventsStorage : EventsStorage = null
  def getSourcedOperations = new EmbeddedSourcedOperations(eventsStorage)
}

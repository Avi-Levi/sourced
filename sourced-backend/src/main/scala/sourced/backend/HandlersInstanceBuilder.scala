package sourced.backend

import sourced.backend.stream.NoOpStream
import sourced.handlers.api.EventsHandler

trait HandlersInstanceBuilder {
  def createHandlerInstance(cls:Class[_]):EventsHandler = {
    val instance = cls.getConstructor().newInstance().asInstanceOf[EventsHandler]
    instance.setStreamRef(NoOpStream)
    instance
  }
}

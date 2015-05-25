package sourced.testing.utils

import sourced.handlers.api.{HandlerMethod, EventsHandler}

class TestHandler extends EventsHandler{
  var dispatchCount = 0
  @HandlerMethod
  def handlerMethod(e:TestEvent) = dispatchCount += 1
  
  def hasStreamRef = this.stream != null
}

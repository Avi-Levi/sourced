package testsClasses

import sourced.handlers.api.{EventsHandler, HandlerMethod}

class TestHandler extends EventsHandler{
  var dispatchCount = 0
  @HandlerMethod
  def handlerMethod(e:TestEvent) = dispatchCount += 1
  
  def hasStreamRef = this.stream != null
}

import sourced.handlers.api.{HandlerMethod, EventsHandler}

class RegisteresAtDispatchRecorderHandler extends EventsHandler{
  @HandlerMethod
  def handlerMethod(e:TestEvent) = DispatchRecorder.count += 1
}
object DispatchRecorder{
  var count = 0
}

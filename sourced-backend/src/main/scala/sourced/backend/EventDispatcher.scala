package sourced.backend

import java.lang.reflect.Method

import sourced.handlers.api.EventsHandler

trait EventDispatcher{
  val topic:String
  def dispatch(msg:AnyRef)
}

class DefaultEventDispatcher(val topic:String,private val method:Method, private val handlerInstance:EventsHandler) extends EventDispatcher{
  override def dispatch(msg: AnyRef) = method.invoke(handlerInstance, msg)
}

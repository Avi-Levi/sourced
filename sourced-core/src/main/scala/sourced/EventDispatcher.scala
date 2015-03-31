package sourced

import java.lang.reflect.Method

trait EventDispatcher{
  val topic:String
  def dispatch(msg:AnyRef)
}

class DefaultEventDispatcher(val topic:String,private val method:Method, private val handlerInstance:AnyRef) extends EventDispatcher{
  override def dispatch(msg: AnyRef) = method.invoke(handlerInstance, msg)
}

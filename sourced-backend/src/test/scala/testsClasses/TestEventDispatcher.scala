package testsClasses

import sourced.backend.EventDispatcher

class TestEventDispatcher(val handler:TestHandler) extends EventDispatcher{

  override def dispatch(msg: AnyRef): Unit = handler.handlerMethod(msg.asInstanceOf[TestEvent])

  override val topic: String = classOf[TestEvent].getName
}

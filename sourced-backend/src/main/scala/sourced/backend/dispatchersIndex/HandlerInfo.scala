package sourced.backend.dispatchersIndex

import java.lang.reflect.Method

case class HandlerInfo(topicToMethodsMap: Map[String, Array[Method]], private val newInstance : () => AnyRef){
  lazy val instance = newInstance()

  private var updated : Boolean = _
  private[sourced] def setUpdated = updated = true
  private[sourced]def isUpdated = updated
}
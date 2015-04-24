package sourced.backend.views

import java.lang.reflect.Method

case class ViewMetadata(viewHandlerClass:Class[_],topicToMethodsMap:Map[String,Array[Method]], createTopic:String, deleteTopic:String)

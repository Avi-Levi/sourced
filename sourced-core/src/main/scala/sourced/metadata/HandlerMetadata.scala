package sourced.metadata

import java.lang.reflect.Method

case class HandlerMetadata(handlerClass:Class[_],topicToMethodsMap:Map[String,Array[Method]]) {}

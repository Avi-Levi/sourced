package sourced.metadata

import java.lang.reflect.Method

import sourced.TopicsUtil
import sourced.api.HandlerMethod
import sourced.api.exceptions.InvalidStructureException

class DefaultHandlerMetadataBuilder extends HandlerMetadataBuilder{
  def forHandlerClasses(handlersClasses:Iterable[Class[_]]) : Array[HandlerMetadata] = handlersClasses.map(buildHandlerMetadata).toArray
  
  private def buildHandlerMetadata(handlerClass:Class[_]) = {
    def isHandlerMethod(m:Method) = !m.getAnnotationsByType(classOf[HandlerMethod]).isEmpty
    def mapTopicToMethod(m:Method) : (String,Method) = {
      if(m.getParameterCount != 1) throw new InvalidStructureException(s"a handler method must receive only one parameter, method: ${m.getName} receives ${m.getParameterCount}")

      val methodTopic = getMethodTopic(m)

      (methodTopic,m)
    }
    def mapTopicToMethods(group: (String, Array[(String, Method)])):(String,Array[Method]) = {
      val (topic,topic_method_arr) = group
      (topic,topic_method_arr.map(_._2))
    }
    def getMethodTopic(m:Method) = TopicsUtil.getClassTopic(m.getParameterTypes.head)

    val topicToMethod =
      handlerClass.getMethods
        .filter(isHandlerMethod)
        .map(mapTopicToMethod)
        .groupBy(_._1)
        .map(mapTopicToMethods)
        .toMap

    HandlerMetadata(handlerClass, topicToMethod)
  }
}

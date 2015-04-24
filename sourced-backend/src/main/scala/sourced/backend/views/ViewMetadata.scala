package sourced.backend.views

import java.lang.reflect.Method

case class ViewMetadata(viewHandlerClass:Class[_], updateTopics:Array[String], createTopic:String, deleteTopic:String, topicToMethodsMap:Map[String,Array[Method]])

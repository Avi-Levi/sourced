package sourced.backend.metadata

import sourced.backend.TopicsUtil

import scala.collection.Iterable
import scala.collection.mutable.ListBuffer

case class StreamMetadata(streamType:String, handlersMetadata:Array[HandlerMetadata]) {
  val locker = new AnyRef
  
  private var classToMetadataMap : Map[Class[_],EventMetadata] = handlersMetadata
    .flatMap(_.topicToMethodsMap.flatMap(_._2))
    .map(_.getParameterTypes.head)
    .map(cls=>(cls,toMetadata(cls)))
    .toMap
  
  def getEventMetadata(eventClass:Class[_]):EventMetadata = {
    this.classToMetadataMap.get(eventClass) match {
      case Some(metadata) => metadata
      case None =>
        addEventMetadata(eventClass)
        classToMetadataMap.get(eventClass).get
    }
  }
  
  def addEventMetadata(eventClass:Class[_]) = synchronized{
    classToMetadataMap.get(eventClass) match {
      case Some(metadata) => metadata
      case None =>
        val oldItems = this.classToMetadataMap.toSeq :+ (eventClass,toMetadata(eventClass))
        classToMetadataMap = Map[Class[_],EventMetadata](oldItems  :_*)
    }
  }
  
  private def toMetadata(cls:Class[_]) = {
    def extractTopics(cls:Class[_]) : Iterable[String] = {
      val topics = ListBuffer[String](TopicsUtil.getClassTopic(cls))

      cls.getInterfaces.foreach(topics ++= extractTopics(_))
      if(cls.getSuperclass != null) topics ++= extractTopics(cls.getSuperclass)

      topics
    }
    
    EventMetadata(extractTopics(cls).toArray)
  }
}

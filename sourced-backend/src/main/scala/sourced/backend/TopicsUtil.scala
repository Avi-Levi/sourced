package sourced.backend

object TopicsUtil {
  def getClassTopic(cls:Class[_]) = cls.getName
}

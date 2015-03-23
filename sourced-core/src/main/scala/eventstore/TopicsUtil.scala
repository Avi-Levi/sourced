package eventstore

object TopicsUtil {
  def getClassTopic(cls:Class[_]) = cls.getName
}

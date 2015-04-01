package sourced.mongodb.serialization

import java.lang.reflect.{Field, Constructor}

import scala.collection.Map

case class ClassMetadata(cls:Class[_],ctor:Constructor[_], fields:Map[String,Field]){}
object ClassMetadata{
  def apply(cls:Class[_]) : ClassMetadata= ClassMetadata(cls, cls.getConstructors.head, cls.getDeclaredFields.map(f=>(f.getName,f)).toMap)
}

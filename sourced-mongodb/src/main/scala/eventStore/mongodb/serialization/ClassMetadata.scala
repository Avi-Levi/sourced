package eventStore.mongodb.serialization

import java.lang.reflect.{Field, Constructor}

import scala.collection.Map

case class ClassMetadata(cls:Class[_],ctor:Constructor[_], fields:Map[String,Field]){
  def newInstance = {
    try{
      val params = ctor.getParameterTypes.map{cls=>
        valueHandlers.get(cls) match {
          case Some(valueHandler) => valueHandler.defaultValue.asInstanceOf[AnyRef]
          case None => null
        }
      }

      ctor.newInstance(params.toSeq :_*).asInstanceOf[AnyRef]
    }catch {
      case t: Throwable => throw new CreateObjectInstanceException(cls,t)
    }
  }
}
object ClassMetadata{
  def apply(cls:Class[_]) : ClassMetadata= ClassMetadata(cls, cls.getConstructors.head, cls.getDeclaredFields.map(f=>(f.getName,f)).toMap)
}

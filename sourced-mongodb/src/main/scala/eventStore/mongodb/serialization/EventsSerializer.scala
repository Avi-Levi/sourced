package eventStore.mongodb.serialization

import java.lang.reflect.Field

import eventstore.events.EventObject
import reactivemongo.bson.{BSONDocument, _}

import scala.collection._

object EventsSerializer {
  val valueHandlers = Map[Class[_], ValueHandler](
    classOf[String] -> ValueHandler("", v => v.asInstanceOf[BSONString].value, v=>BSONString(v.asInstanceOf[String])),
    classOf[Long] -> ValueHandler(0L, v => v.asInstanceOf[BSONLong].value, v=>BSONLong(v.asInstanceOf[Long])),
    classOf[Int] -> ValueHandler(0, v => v.asInstanceOf[BSONInteger].value, v=>BSONInteger(v.asInstanceOf[Int])),
    classOf[Boolean] -> ValueHandler(false, v => v.asInstanceOf[BSONBoolean].value, v=>BSONBoolean(v.asInstanceOf[Boolean]))
  )
  val classesMetadata = mutable.Map[String, ClassMetadata]()

  def toEventObject(doc:BSONDocument) : EventObject = {
    def deserializeDocument(className:String, body:BSONDocument) : AnyRef = {
      val metadata = getMetadata(className)
      val instance = newInstance(metadata)
      body.elements.foreach{elem=>
        metadata.fields.get(elem._1).map(f=>{
          val value = if(!elem._2.isInstanceOf[BSONDocument]) {
            valueHandlers.get(f.getType).get.toValue(elem._2)
          } else{
            deserializeDocument(f.getType.getName, elem._2.asInstanceOf[BSONDocument])
          }

          setField(instance, f, value)
        })
      }
      instance
    }
    def newInstance(metadata:ClassMetadata) = {
      try{
        val params = metadata.ctor.getParameterTypes.map{cls=>
          valueHandlers.get(cls) match {
            case Some(valueHandler) => valueHandler.defaultValue.asInstanceOf[AnyRef]
            case None => null
          }
        }

        metadata.ctor.newInstance(params.toSeq :_*).asInstanceOf[AnyRef]
      }catch {
        case t: Throwable => throw new CreateObjectInstanceException(metadata.cls,t)
      }
    }
    
    EventObject(
      doc.getAs[Long]("index").get,
      doc.getAs[String]("className").get,
      deserializeDocument(doc.getAs[String]("className").get, doc.getAs[BSONDocument]("body").get)
    )
  }

  def toDocument(e:EventObject) : BSONDocument = {
    def serializeObject(cls:Class[_], instance:AnyRef) : BSONDocument = {
      val metadata = getMetadata(cls.getName)

      val values = metadata.fields.map{f=>
        val v = getFieldValue(instance, f._2)
        val bsonValue = valueHandlers.get(f._2.getType) match {
          case Some(ser) => ser.toBSON(v)
          case None => serializeObject(v.getClass,v)
        }
        ((f._1, bsonValue))
      }
      BSONDocument(values.toList)
    }

    BSONDocument(
      "index" -> e.index,
      "className" -> e.className,
      "body" -> serializeObject(e.body.getClass,e.body)
    )
  }

  private def setField(instance: AnyRef, f: Field, value: Any): Unit = {
    val accessible = f.isAccessible
    f.setAccessible(true)
    f.set(instance, value)
    f.setAccessible(accessible)
  }
  private def getFieldValue(instance: AnyRef, f: Field): AnyRef = {
    val accessible = f.isAccessible
    f.setAccessible(true)
    val res = f.get(instance)
    f.setAccessible(accessible)
    res
  }
  private def getMetadata(className:String) = classesMetadata synchronized{classesMetadata.getOrElseUpdate(className, ClassMetadata(Class.forName(className)))}
}

package sourced.mongodb.eventsStorage

import reactivemongo.bson.{BSONDocument, BSONString}

object BSONDocumentExtensions {
  implicit class ExtendedBSONDocument(doc : BSONDocument){
    def setStreamId(streamId:String) = doc.add("streamId" -> BSONString(streamId))
  }
}

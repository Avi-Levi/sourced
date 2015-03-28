package eventStore.mongodb

import reactivemongo.bson.{BSONString, BSONDocument}

object BSONDocumentExtensions {
  implicit class ExtendedBSONDocument(doc : BSONDocument){
    def setStreamId(streamId:String) = doc.add("streamId" -> BSONString(streamId))
  }
}

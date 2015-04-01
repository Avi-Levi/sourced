package sourced.mongodb.eventsStorage

case class MongoConfig(dbName:String, collectionName:String,nodes : Seq[String] = Seq("localhost")) {}

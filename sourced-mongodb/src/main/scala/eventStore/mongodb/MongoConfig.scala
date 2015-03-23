package eventStore.mongodb

case class MongoConfig(dbName:String, collectionName:String,nodes : Seq[String] = Seq("localhost")) {}

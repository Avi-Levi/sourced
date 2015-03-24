package eventStore.mongodb

case class PersistedEventObject(index:Long, className:String, body:String){}

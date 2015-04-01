import eventStore.mongodb.MongoConfig
import org.scalatest.FunSuite
import sourced.embedded.{EmbeddedSourced, StreamDefinition}
import sourced.mongodb.MongoEventsStorage
import sourced.mongodb.eventsStorage.{MongoEventsStorage, MongoConfig}

class EmbeddedSourcedTests extends FunSuite{
  test("api"){
    import sourced.embedded.Implicits._

    EmbeddedSourced.registerStream(StreamDefinition("",List[Class[_]]()))

    implicit val mongoEventsStorage = new MongoEventsStorage(MongoConfig("",""))

    val ops = EmbeddedSourced.getSourcedOperations()
  }
}

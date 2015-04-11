import org.scalatest.FunSuite
import sourced.embedded.{EmbeddedSourced, StreamDefinition}
import sourced.mongodb.eventsStorage.{MongoConfig, MongoEventsStorage}

class EmbeddedSourcedTests extends FunSuite{
  test("api"){
    import sourced.embedded.Implicits._

    EmbeddedSourced.registerStream(StreamDefinition("",List[Class[_]]()))

    implicit val mongoEventsStorage = new MongoEventsStorage(MongoConfig("",""))

    val ops = EmbeddedSourced.getSourcedOperations()
  }
}

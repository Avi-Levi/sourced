import org.scalatest.FunSuite
import sourced.embedded.{EmbeddedSourcedConfiguration, StreamDefinition}
import sourced.mongodb.eventsStorage.{MongoConfig, MongoEventsStorage}

class EmbeddedSourcedTests extends FunSuite{
  test("api"){
    import sourced.embedded.Implicits._

    EmbeddedSourcedConfiguration.registerStream(StreamDefinition("",List[Class[_]]()))

    implicit val mongoEventsStorage = new MongoEventsStorage(MongoConfig("",""))

    val ops = EmbeddedSourcedConfiguration.getSourcedOperations()
  }
}

import java.util.UUID

import org.scalatest.FunSuite
import sourced.client.api.StreamKey
import sourced.embedded.{StreamDefinition, EmbeddedSourcedConfiguration}
import sourced.testing.utils.{TestEvent, TestHandler, TestEventsStorage}
import sourced.embedded.Implicits._

class IntegrationTests extends FunSuite{
  val streamType: String = "test-stream"
  test("works"){
    val config = new EmbeddedSourcedConfiguration(new TestEventsStorage(List()))
    config.registerStream(StreamDefinition(streamType, Array(classOf[TestHandler])))
    val clientFactory = config.newClientFactory()
    val client = clientFactory.streamClient(StreamKey(UUID.randomUUID().toString,streamType))
    client.get.push(TestEvent())
  }
}

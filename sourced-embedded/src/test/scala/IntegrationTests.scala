import java.util.UUID
import java.util.concurrent.TimeUnit

import org.scalatest.FunSuite
import sourced.client.api.StreamKey
import sourced.embedded.Implicits._
import sourced.embedded._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class IntegrationTests extends FunSuite{
  val streamType: String = "test-stream"
  test("works"){
    val config = new EmbeddedSourcedConfiguration(new TestEventsStorage(List()))
    config.registerStream(StreamDefinition(streamType, Array(classOf[RegisteresAtDispatchRecorderHandler])))
    val clientFactory = config.newClientFactory()
    val client = clientFactory.streamClient(StreamKey(UUID.randomUUID().toString,streamType)).get

    val f = client.push(TestEvent())

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(DispatchRecorder.count == 1)
  }
}

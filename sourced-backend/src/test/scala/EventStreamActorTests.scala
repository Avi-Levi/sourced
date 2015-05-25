import java.util.concurrent.TimeUnit

import StreamActorExtensions._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite}
import sourced.backend.api.EventObject
import sourced.backend.metadata.{HandlerMetadata, StreamMetadata}
import sourced.backend.stream.EventStreamActor
import sourced.messages.PushEvents
import sourced.testing.utils.{TestEvent, TestHandler, TestEventsStorage}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, _}

class EventStreamActorTests extends FunSuite with MockFactory with BeforeAndAfter {
  val streamId = "someid"
  val streamType = "sometype"

  var eventsStorage: TestEventsStorage = null

  var handlerMetadata: HandlerMetadata = null
  var streamMetadata: StreamMetadata = null

  implicit val actorSystem = ActorSystem("test-system")
  implicit val executionContext = actorSystem.dispatcher
  implicit val timeout = Timeout(1 second)

    before{
      this.eventsStorage = new TestEventsStorage(List())

      this.handlerMetadata = HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))
      this.streamMetadata = StreamMetadata(streamType, Array(handlerMetadata))
    }
  test("state is loaded correctly"){
    val actor = TestActorRef(new EventStreamActor(streamId,streamMetadata, new TestEventsStorage(List(EventObject(0, "a", TestEvent())))))


    assert(actor.index.topicToDispatchersMap.keys.head.equals(classOf[TestEvent].getName))
    assert(actor.index.topicToDispatchersMap.flatMap(_._2).size == 1)
    assert(actor.index.handlersInstances.size == 1)
    assert(actor.index.handlersInstances.head.instance.getClass.equals(classOf[TestHandler]))
    assert(actor.index.handlersInstances.head.instance.asInstanceOf[TestHandler].dispatchCount == 1)
  }
    test("a published event is dispatched to a handler once"){
      val actor = TestActorRef(new EventStreamActor(streamId,streamMetadata, eventsStorage))
      val f = actor ? PushEvents("",Array(TestEvent()))

      Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

      assert(actor.headTestHandler.dispatchCount == 1)
    }
  test("a published event is dispatched to a handler when the state is loaded async"){

    implicit val executionContext = actorSystem.dispatcher

    val actor = TestActorRef(new EventStreamActor(streamId, streamMetadata, new TestEventsStorage(List(),10)))

    val f = actor ? PushEvents("",Array(TestEvent()))

    Await.ready(f,Duration(30,TimeUnit.MILLISECONDS))

    assert(actor.headTestHandler.dispatchCount == 1)
  }
  test("stream ref is injected"){
    val actor = TestActorRef(new EventStreamActor(streamId, streamMetadata, eventsStorage))

    val f = actor ? PushEvents("",Array(TestEvent()))

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(actor.headTestHandler.hasStreamRef)
  }
  test("dispatching event with no handlers listeners doesn't fail"){
    val actor = TestActorRef(new EventStreamActor(streamId, streamMetadata, eventsStorage))

    val f = actor ? PushEvents("",Array(TestEvent()))

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(true)
  }
  test("new events are forwarded to events repository"){
    val actor = TestActorRef(new EventStreamActor(streamId, streamMetadata, eventsStorage))

    val f = actor ? PushEvents("",Array(TestEvent()))

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(eventsStorage.newEvents != null)
    assert(eventsStorage.newEvents.size == 1)
  }
}

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import sourced.messages.PushEvents
import scala.concurrent.duration._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite}
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.metadata.{HandlerMetadata, StreamMetadata}
import sourced.backend.stateLoader.{LoadStateResponse, StreamStateLoader}
import sourced.backend.stream.EventStreamActor
import testsClasses.{TestEvent, TestEventsStorage, TestHandler}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class EventStreamActorTests extends FunSuite with MockFactory with BeforeAndAfter {
  val streamId = "someid"
  val streamType = "sometype"

  var streamStateLoader: StreamStateLoader = null
  var eventsRepository: TestEventsStorage = null

  var handlerMetadata: HandlerMetadata = null
  var streamMetadata: StreamMetadata = null
  var handler : TestHandler = null
  var handlersIndex : TopicsToStreamHandlersIndex = null

  implicit val actorSystem = ActorSystem("test-system")
  implicit val timeout = Timeout(1 second)

  before{
    this.streamStateLoader = stub[StreamStateLoader]
    this.eventsRepository = new TestEventsStorage(List())

    this.handlerMetadata = HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))
    this.streamMetadata = StreamMetadata(streamType, Array(handlerMetadata))
    this.handlersIndex = new TopicsToStreamHandlersIndex(Array(this.handlerMetadata).map(h=>HandlerInfo(h.topicToMethodsMap,()=>new TestHandler)),streamMetadata.getEventMetadata)
    this.handler = this.handlersIndex.handlersInstances.head.instance.asInstanceOf[TestHandler]
  }

  test("a published event is dispatched to a handler once"){

    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val actor = TestActorRef(new EventStreamActor(streamId,streamStateLoader, streamMetadata, eventsRepository))

    val f = actor ? PushEvents(streamId,"",Array(TestEvent()))
    
    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(handler.dispatchCount == 1)
  }
  test("stream ref is injected"){

    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val actor = TestActorRef(new EventStreamActor(streamId,streamStateLoader, streamMetadata, eventsRepository))

    val f = actor ? PushEvents(streamId,"",Array(TestEvent()))

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(handler.hasStreamRef)
  }
  test("dispatching event with no handlers listeners doesn't fail"){
    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val actor = TestActorRef(new EventStreamActor(streamId,streamStateLoader, streamMetadata, eventsRepository))

    val f = actor ? PushEvents(streamId,"",Array(TestEvent()))

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(true)
  }
  test("new events are forwarded to events repository"){
    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val actor = TestActorRef(new EventStreamActor(streamId,streamStateLoader, streamMetadata, eventsRepository))

    val f = actor ? PushEvents(streamId,"",Array(TestEvent()))

    Await.ready(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(eventsRepository.newEvents != null)
    assert(eventsRepository.newEvents.size == 1)
  }
}

import java.util.concurrent.TimeUnit

import sourced.metadata.{HandlerMetadata, StreamMetadata}
import sourced.stateLoader.{LoadStateResponse, StreamStateLoader}
import sourced.stream.{DefaultEventStream, StreamKey}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite}
import testsClasses.{TestEvent, TestEventDispatcher, TestEventsRepository, TestHandler}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class DefaultEventStreamTests extends FunSuite with MockFactory with BeforeAndAfter{
  val streamId = "someid"
  val streamType = "sometype"

  var streamStateLoader: StreamStateLoader = null
  var eventsRepository: TestEventsRepository = null

  var handlerMetadata: HandlerMetadata = null
  var streamMetadata: StreamMetadata = null
  var handler = new TestHandler
  var dispatcher: TestEventDispatcher = null
  var handlers: List[(HandlerMetadata, TestHandler)] = null
  var dispatchers: Map[String, List[TestEventDispatcher]] = null

  before{
    this.streamStateLoader = stub[StreamStateLoader]
    this.eventsRepository = new TestEventsRepository(List())

    this.handlerMetadata = HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))
    this.streamMetadata = StreamMetadata(streamType, Array(handlerMetadata))
    this.handler = new TestHandler
    this.dispatcher = new TestEventDispatcher(handler)
    this.handlers = List((handlerMetadata,handler))
    this.dispatchers = Map(classOf[TestEvent].getName -> List(dispatcher))
  }
  test("a published event is dispatched to a handler"){

    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,dispatchers,handlers)))

        val stream = new DefaultEventStream(StreamKey(streamId,streamType),streamStateLoader, streamMetadata, eventsRepository)

    Await.ready(stream.push(TestEvent()),Duration(100,TimeUnit.MILLISECONDS))

    assert(handler.testEventDispatched)
  }
  test("stream ref is injected"){

    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,dispatchers,handlers)))

    val stream = new DefaultEventStream(StreamKey(streamId,streamType), streamStateLoader, streamMetadata, eventsRepository)
    
    Await.ready(stream.push(new AnyRef),Duration(100,TimeUnit.MILLISECONDS))
    
    assert(handler.hasStreamRef)
  }
  test("dispatching event with no handlers listeners doesn't fail"){
    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,dispatchers,handlers)))

    val stream = new DefaultEventStream(StreamKey(streamId,streamType), streamStateLoader, streamMetadata, eventsRepository)
    Await.ready(stream.push(new AnyRef),Duration(100,TimeUnit.MILLISECONDS))

    assert(true)
  }
  test("new events are forwarded to events repository"){
    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,dispatchers,handlers)))

    val stream = new DefaultEventStream(StreamKey(streamId,streamType), streamStateLoader, streamMetadata, eventsRepository)
    Await.ready(stream.push(new AnyRef),Duration(100,TimeUnit.MILLISECONDS))

    assert(eventsRepository.newEvents != null)
    assert(eventsRepository.newEvents.size == 1)
  }
}

import java.util.concurrent.TimeUnit

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite}
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.metadata.{HandlerMetadata, StreamMetadata}
import sourced.backend.stateLoader.{LoadStateResponse, StreamStateLoader}
import sourced.backend.stream.DefaultEventStream
import testsClasses.{TestEvent, TestEventsStorage, TestHandler}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class DefaultEventStreamTests extends FunSuite with MockFactory with BeforeAndAfter{
  val streamId = "someid"
  val streamType = "sometype"

  var streamStateLoader: StreamStateLoader = null
  var eventsRepository: TestEventsStorage = null

  var handlerMetadata: HandlerMetadata = null
  var streamMetadata: StreamMetadata = null
  var handler : TestHandler = null
  var handlersIndex : TopicsToStreamHandlersIndex = null

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

        val stream = new DefaultEventStream(streamId,streamStateLoader, streamMetadata, eventsRepository)

    Await.ready(stream.push(TestEvent()),Duration(100,TimeUnit.MILLISECONDS))

    assert(handler.dispatchCount == 1)
  }
  test("stream ref is injected"){

    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val stream = new DefaultEventStream(streamId, streamStateLoader, streamMetadata, eventsRepository)
    
    Await.ready(stream.push(new AnyRef),Duration(100,TimeUnit.MILLISECONDS))
    
    assert(handler.hasStreamRef)
  }
  test("dispatching event with no handlers listeners doesn't fail"){
    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val stream = new DefaultEventStream(streamId, streamStateLoader, streamMetadata, eventsRepository)
    Await.ready(stream.push(new AnyRef),Duration(100,TimeUnit.MILLISECONDS))

    assert(true)
  }
  test("new events are forwarded to events repository"){
    (streamStateLoader.loadStreamState _).when(streamId,streamMetadata).returns(Future.successful(LoadStateResponse(10,this.handlersIndex)))

    val stream = new DefaultEventStream(streamId, streamStateLoader, streamMetadata, eventsRepository)
    Await.ready(stream.push(new AnyRef),Duration(100,TimeUnit.MILLISECONDS))

    assert(eventsRepository.newEvents != null)
    assert(eventsRepository.newEvents.size == 1)
  }
}

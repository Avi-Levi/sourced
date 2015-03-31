import java.util.concurrent.TimeUnit

import sourced.api.HandlersInstanceBuilder
import sourced.events.EventObject
import sourced.metadata.{HandlerMetadata, StreamMetadata}
import sourced.stateLoader.DefaultStreamStateLoader
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import testsClasses.{TestEvent, TestEventsRepository, TestHandler}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DefaultStreamStateLoaderTests extends FunSuite with MockFactory {
  test("state is loaded"){

    val streamType = "test"
    val streamMetadata = StreamMetadata(streamType,Array(HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))))
    val streamTypeToHandlersMetadata = Map(streamType -> streamMetadata)
    val eventsRepository = new TestEventsRepository(List(EventObject(0, "a", TestEvent())))

    val handlersInstanceBuilder = new AnyRef with HandlersInstanceBuilder

    val loader = new DefaultStreamStateLoader(eventsRepository)

    val f = loader.loadStreamState(streamType, streamMetadata)

    val res = Await.result(f,Duration(100,TimeUnit.MILLISECONDS))

    assert(res.handlers.size == 1)
    assert(res.eventDispatchers.size == 1)
    assert(res.eventDispatchers.head._1.equals(classOf[TestEvent].getName))
    assert(res.eventDispatchers.head._2.size == 1)
    assert(res.eventDispatchers.head._2.head.topic.equals(classOf[TestEvent].getName))
    assert(res.handlers.size == 1)
    assert(res.handlers.head._1.handlerClass.equals(classOf[TestHandler]))
    assert(res.handlers.head._2.getClass.equals(classOf[TestHandler]))
    assert(res.handlers.head._2.asInstanceOf[TestHandler].testEventDispatched)
  }
}

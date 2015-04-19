import java.util.concurrent.TimeUnit

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import sourced.backend.HandlersInstanceBuilder
import sourced.backend.events.EventObject
import sourced.backend.metadata.{HandlerMetadata, StreamMetadata}
import sourced.backend.stateLoader.DefaultStreamStateLoader
import testsClasses.{TestEvent, TestEventsStorage, TestHandler}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DefaultStreamStateLoaderTests extends FunSuite with MockFactory {
  test("state is loaded"){

    val streamType = "test"
    val streamMetadata = StreamMetadata(streamType,Array(HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))))
    val streamTypeToHandlersMetadata = Map(streamType -> streamMetadata)
    val testEventsStorage = new TestEventsStorage(List(EventObject(0, "a", TestEvent())))

    val handlersInstanceBuilder = new AnyRef with HandlersInstanceBuilder

    val loader = new DefaultStreamStateLoader(testEventsStorage)

    val f = loader.loadStreamState(streamType, streamMetadata)

    val res = Await.result(f,Duration(100,TimeUnit.MILLISECONDS))


    assert(res.handlersIndex.topicToDispatchersMap.keys.head.equals(classOf[TestEvent].getName))
    assert(res.handlersIndex.topicToDispatchersMap.flatMap(_._2).size == 1)

    assert(res.handlersIndex.handlersInstances.size == 1)
    assert(res.handlersIndex.handlersInstances.head.getClass.equals(classOf[TestHandler]))
    assert(res.handlersIndex.handlersInstances.head.asInstanceOf[TestHandler].dispatchCount == 1)
  }
}

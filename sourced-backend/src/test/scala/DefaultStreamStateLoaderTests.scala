/*
import java.util.concurrent.TimeUnit

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import sourced.backend.HandlersFactory
import sourced.backend.dispatchersIndex.TopicsToStreamHandlersIndex
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
    val testEventsStorage = new TestEventsStorage(List(EventObject(0, "a", TestEvent())))

    val handlersInstanceBuilder = new AnyRef with HandlersFactory

    val loader = new DefaultStreamStateLoader(testEventsStorage)

    val f = loader.loadStreamState(streamType, streamMetadata)

    val handlersIndex = Await.result(f,Duration(100,TimeUnit.MILLISECONDS)).handlersIndex.asInstanceOf[TopicsToStreamHandlersIndex]


    assert(handlersIndex.topicToDispatchersMap.keys.head.equals(classOf[TestEvent].getName))
    assert(handlersIndex.topicToDispatchersMap.flatMap(_._2).size == 1)

    assert(handlersIndex.handlersInstances.size == 1)
    assert(handlersIndex.handlersInstances.head.instance.getClass.equals(classOf[TestHandler]))
    assert(handlersIndex.handlersInstances.head.instance.asInstanceOf[TestHandler].dispatchCount == 1)
  }
}
*/

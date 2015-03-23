import eventstore.api.HandlersInstanceBuilder
import eventstore.events.EventObject
import eventstore.metadata.{StreamMetadata, HandlerMetadata}
import eventstore.stateLoader.DefaultStreamStateLoader
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import testsClasses.{TestEvent, TestEventsRepository, TestHandler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class DefaultStreamStateLoaderTests extends FunSuite with MockFactory {
  test("state is loaded"){

    val streamType = "test"
    val streamMetadata = StreamMetadata(streamType,Array(HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))))
    val streamTypeToHandlersMetadata = Map(streamType -> streamMetadata)
    val eventsRepository = new TestEventsRepository(List(EventObject(0, "a", TestEvent())))

    val handlersInstanceBuilder = new AnyRef with HandlersInstanceBuilder

    val loader = new DefaultStreamStateLoader(eventsRepository,handlersInstanceBuilder, streamMetadata)

    loader.loadStreamState(streamType, streamMetadata) onComplete{
      case Success(res) =>
        assert(res.handlers.size == 1)
        assert(res.eventDispatchers.size == 1)
        assert(res.eventDispatchers.head._1.equals(classOf[TestEvent].getName))
        assert(res.eventDispatchers.head._2.size == 1)
        assert(res.eventDispatchers.head._2.head.topic.equals(classOf[TestEvent].getName))
        assert(res.handlers.size == 1)
        assert(res.handlers.head._1.handlerClass.equals(classOf[TestHandler]))
        assert(res.handlers.head._2.getClass.equals(classOf[TestHandler]))
        assert(res.handlers.head._2.asInstanceOf[TestHandler].testEventDispatched)
      case Failure(t) => ???
    }
  }
}

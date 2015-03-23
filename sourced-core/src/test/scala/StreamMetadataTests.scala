import eventstore.metadata.{HandlerMetadata, StreamMetadata}
import org.scalatest.{BeforeAndAfter, FunSuite}
import testsClasses.{TestEvent2, TestHandler, TestEvent}

class StreamMetadataTests extends FunSuite with BeforeAndAfter{
  var handlerMetadata: HandlerMetadata = null
  var streamMetadata: StreamMetadata = null

  before{
    this.handlerMetadata = HandlerMetadata(classOf[TestHandler],Map(classOf[TestEvent].getName -> Array(classOf[TestHandler].getMethod("handlerMethod",classOf[TestEvent]))))
    this.streamMetadata = StreamMetadata("", Array(handlerMetadata))
  }
  test("simple event is registered automatically by inspecting handlers"){
    val eventMetadata = streamMetadata.getEventMetadata(classOf[TestEvent])

    assert(eventMetadata.topics.contains(classOf[TestEvent].getName))
    assert(eventMetadata.topics.contains(classOf[AnyRef].getName))
  }
  test("event is registered when it doesn't exist in handlers to inspect"){
    val streamMetadata = new StreamMetadata("", Array[HandlerMetadata]())
    val eventMetadata = streamMetadata.getEventMetadata(classOf[TestEvent2])

    assert(eventMetadata.topics.contains(classOf[TestEvent2].getName))
    assert(eventMetadata.topics.contains(classOf[AnyRef].getName))
  }

  test("register dynamic event class"){

    trait t{}
    val instance = new TestEvent with t 
    val eventClass = instance.getClass

    val eventMetadata = streamMetadata.getEventMetadata(eventClass)

    assert(eventMetadata.topics.contains(classOf[TestEvent].getName))
    assert(eventMetadata.topics.contains(classOf[AnyRef].getName))
    assert(eventMetadata.topics.contains(classOf[t].getName))
  }
}

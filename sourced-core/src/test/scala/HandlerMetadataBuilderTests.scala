import sourced.metadata.DefaultHandlerMetadataBuilder
import org.scalatest.FunSuite
import testsClasses.{TestEvent, TestHandler}

class HandlerMetadataBuilderTests extends FunSuite{
  test("build simple handler metadata"){
    val builder= new DefaultHandlerMetadataBuilder()

    val metadata = builder.forHandlerClasses(List(classOf[TestHandler]))

    assert(metadata.size == 1)
    assert(metadata.head.handlerClass.equals(classOf[TestHandler]))
    assert(metadata.head.topicToMethodsMap.size == 1)
    assert(metadata.head.topicToMethodsMap.get(classOf[TestEvent].getName).isDefined)
    assert(metadata.head.topicToMethodsMap.get(classOf[TestEvent].getName).get.size == 1)
    assert(metadata.head.topicToMethodsMap.get(classOf[TestEvent].getName).get.head.getName.equals("handlerMethod"))
  }
}

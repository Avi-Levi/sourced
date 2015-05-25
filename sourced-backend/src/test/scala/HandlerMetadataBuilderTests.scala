import org.scalatest.FunSuite
import sourced.backend.metadata.DefaultHandlerMetadataBuilder

class HandlerMetadataBuilderTests extends FunSuite{
  test("build simple handler metadata"){
    val builder= new DefaultHandlerMetadataBuilder()

    val metadata = builder.forHandlerClasses(List(classOf[TestHandler]))

    assert(metadata.length == 1)
    assert(metadata.head.handlerClass.equals(classOf[TestHandler]))
    assert(metadata.head.topicToMethodsMap.size == 1)
    assert(metadata.head.topicToMethodsMap.get(classOf[TestEvent].getName).isDefined)
    assert(metadata.head.topicToMethodsMap.get(classOf[TestEvent].getName).get.length == 1)
    assert(metadata.head.topicToMethodsMap.get(classOf[TestEvent].getName).get.head.getName.equals("handlerMethod"))
  }
}

import org.scalatest.FunSuite
import sourced.backend.HandlersFactory

class HandlersInstanceBuilderTests extends FunSuite{
  
  test("created instance equals to provided class"){
    
    val instanceBuilder = new AnyRef with HandlersFactory
    val inst = instanceBuilder.createHandlerInstance(classOf[testsClasses.TestHandler])
    
    assert(inst.isInstanceOf[testsClasses.TestHandler])
  }
}

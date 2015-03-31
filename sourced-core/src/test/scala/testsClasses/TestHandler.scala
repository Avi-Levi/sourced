package testsClasses

import sourced.WithStreamRef
import sourced.api.HandlerMethod

class TestHandler extends WithStreamRef{
  var testEventDispatched = false
  @HandlerMethod
  def handlerMethod(e:TestEvent) = testEventDispatched = true
  
  def hasStreamRef = this.stream != null
}

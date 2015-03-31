import eventStore.mongodb.serialization.EventsSerializer
import sourced.events.EventObject
import org.scalatest.FunSuite

case class TestCaseClass(xString:String="", xInt:Int=0, xLong:Long=0L, xBoolean: Boolean=false)

class SerializerTests extends FunSuite{
  test("basic serialization"){
    val inst = TestCaseClass("ssss", 111, 222, true)
    val doc = EventsSerializer.toDocument(EventObject(1,classOf[TestCaseClass].getName,inst))
    val inst2 = EventsSerializer.toEventObject(doc).body.asInstanceOf[TestCaseClass]

    assert(inst.xBoolean == inst2.xBoolean)
    assert(inst.xInt == inst2.xInt)
    assert(inst.xLong == inst2.xLong)
    assert(inst.xString == inst2.xString)
  }
}

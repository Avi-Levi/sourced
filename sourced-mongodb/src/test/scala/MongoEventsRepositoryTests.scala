import java.util.concurrent.TimeUnit

import eventStore.mongodb.MongoConfig
import org.scalatest.FunSuite
import sourced.backend.events.EventObject
import sourced.mongodb.MongoEventsStorage
import sourced.mongodb.eventsStorage.{MongoEventsStorage, MongoConfig}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class MongoEventsRepositoryTests extends FunSuite{
  val streamId = "someId"
  test("test save events"){
    val repository = new MongoEventsStorage(MongoConfig("sourced","events", Seq("localhost:27017")))
    time{
      val f = repository.save(streamId,Seq(EventObject(0,classOf[TestCaseClass].getName, TestCaseClass("xstr",7,8L,true)))) andThen {
        case x=> repository.iterate(streamId,e=>{
          assert(e.body.asInstanceOf[TestCaseClass].xInt == 7)
        })
      }
      Await.ready(f,Duration(3000,TimeUnit.MILLISECONDS))
    }
  }

  def time[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + "ms")
    result
  }
}

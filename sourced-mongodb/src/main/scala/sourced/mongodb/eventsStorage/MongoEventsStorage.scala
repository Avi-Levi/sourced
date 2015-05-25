package sourced.mongodb.eventsStorage

import play.api.libs.iteratee.{Enumerator, Iteratee}
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, _}
import sourced.backend.api.{EventObject, EventsStorage}
import sourced.mongodb.serialization.EventsSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}
import scala.util.{Failure, Success}

class MongoEventsStorage(private val config: MongoConfig) extends EventsStorage{

  val collection = connect(config)

  override def iterate(streamId: String, handleEvent: (EventObject) => Unit): Future[Long] = {
    val p = promise[Long]()

    var count = 0

    val q = BSONDocument("streamId" -> streamId)
    collection
      .find(q)
      .cursor[BSONDocument]
      .enumerate().apply(Iteratee.foreach{doc =>
        count += 1
        handleEvent(EventsSerializer.toEventObject(doc))
      })
      .onComplete{
      case Success(x) => p.success(count)
      case Failure(t) => p.failure(t)
    }

    p.future
  }

  override def save(streamId:String, events: Iterable[EventObject]): Future[Unit] = {
    import BSONDocumentExtensions._

    collection.bulkInsert(Enumerator.enumerate(events.map(EventsSerializer.toDocument(_).setStreamId(streamId)))).map(x=>Unit)
  }

  private def connect(config: MongoConfig) : BSONCollection = {
    val driver = new MongoDriver
    val connection = driver.connection(config.nodes)
    val db = connection(config.dbName)
    db.collection[BSONCollection](config.collectionName)
  }
}